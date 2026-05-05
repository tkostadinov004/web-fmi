package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotInTicketException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyDeletedException;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
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

    @ExceptionHandler({AlreadyExistsException.class, UserAlreadyLoggedException.class,
            UserAlreadyDeletedException.class, AlreadyChangedPasswordException.class, UserNotPartOfProjectException.class,
            TicketCommentNotInTicketException.class, TicketNotInProjectException.class})
    public ResponseEntity<ErrorResponse> handleConflictErrors(Exception ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({WrongCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUserLogicError(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleDeniedAccess(AuthorizationDeniedException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(OutputMessages.System.ACCESS_DENIED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleValidationFailure(Exception ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(new ValidationErrorResponse(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        LOGGER.error(ex.getMessage());
        String name = ex.getParameterName();

        return new ResponseEntity<>(new ErrorResponse(ValidationConstants.missingParam(name)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<ErrorResponse> handleMiscAuthException(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MultipartException.class, FileServiceException.class})
    public ResponseEntity<ErrorResponse> handleFileExceptions(Exception ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ExceptionMessages.File.invalidFile()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(OutputMessages.System.UNEXPECTED_SERVER_ERROR));
    }
}
