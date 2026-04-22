package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponse;

@RestControllerAdvice
public class ExceptionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler({UserAlreadyExistsException.class, UserAlreadyLoggedException.class, WrongCredentialsException.class})
    public ResponseEntity<String> handleUserLogicError(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        LOGGER.error(ex.getMessage());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleDeniedAccess(AuthorizationDeniedException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(OutputMessages.System.ACCESS_DENIED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(buildErrorResponse(OutputMessages.System.UNEXPECTED_SERVER_ERROR));
    }
}
