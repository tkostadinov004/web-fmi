package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyDeletedException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponse;

@RestControllerAdvice
public class ExceptionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler({UserAlreadyExistsException.class, UserAlreadyLoggedException.class, UserAlreadyDeletedException.class, AlreadyChangedPasswordException.class})
    public ResponseEntity<String> handleConflictErrors(Exception ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({WrongCredentialsException.class})
    public ResponseEntity<String> handleUserLogicError(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleDeniedAccess(AuthorizationDeniedException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(OutputMessages.System.ACCESS_DENIED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<String> handleValidationFailure(Exception ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        LOGGER.error(ex.getMessage());
        String name = ex.getParameterName();

        return new ResponseEntity<>(Map.of("error", ValidationConstants.missingParam(name)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<String> handleMiscAuthException(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ExceptionMessages.File.invalidFile()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(buildErrorResponse(OutputMessages.System.UNEXPECTED_SERVER_ERROR));
    }
}
