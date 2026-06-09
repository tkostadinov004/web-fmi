package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.InvalidWorkflowTransitionException;
import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.OwnershipMismatchException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import bg.sofia.uni.fmi.issuetracker.exception.file.InvalidFileFormatException;
import bg.sofia.uni.fmi.issuetracker.exception.project.InvalidWorkflowException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UnauthorizedProjectModificationException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.UnassignedTicketException;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponse;

@RestControllerAdvice
public class ExceptionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler({AlreadyExistsException.class, UserAlreadyLoggedException.class,
            AlreadyChangedPasswordException.class, UserNotPartOfProjectException.class,
            TicketNotInProjectException.class, ProjectUserAlreadyInProjectException.class,
            UnassignedTicketException.class, InvalidWorkflowTransitionException.class})
    public ResponseEntity<ErrorResponse> handleConflictErrors(Exception ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({OwnershipMismatchException.class})
    public ResponseEntity<ErrorResponse> handleOwnershipMismatch(OwnershipMismatchException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
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

    @ExceptionHandler(UnauthorizedProjectModificationException.class)
    public ResponseEntity<ErrorResponse> handleCannotAddUserToProjectException(UnauthorizedProjectModificationException ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
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

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv -> {
            String fieldName = "";
            for (Path.Node node : cv.getPropertyPath()) {
                fieldName = node.getName();
            }

            errors.put(fieldName, cv.getMessage());
        });

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        LOGGER.error(ex.getMessage());
        String name = ex.getParameterName();

        return ResponseEntity.badRequest().body(buildErrorResponse(ValidationConstants.missingParam(name)));
    }

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<ErrorResponse> handleMiscAuthException(AuthException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ExceptionMessages.File.invalidFile()));
    }

    @ExceptionHandler({FileServiceException.class})
    public ResponseEntity<ErrorResponse> handleFileExceptions(FileServiceException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<ErrorResponse> handleFileOutOfBounds(MaxUploadSizeExceededException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ExceptionMessages.File.sizeExceeded()));
    }

    @ExceptionHandler({InvalidFileFormatException.class})
    public ResponseEntity<ErrorResponse> handleInvalidFileFormat(InvalidFileFormatException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({InvalidWorkflowException.class})
    public ResponseEntity<ErrorResponse> handleInvalidWorkflow(InvalidWorkflowException ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleInvalidUrl(Exception ex) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(ExceptionMessages.invalidUrl()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleJacksonErrors(HttpMessageNotReadableException ex) {
        LOGGER.error(ex.getMessage());
        DateTimeParseException dtex = findCause(ex, DateTimeParseException.class);
        if (dtex != null) {
            return new ResponseEntity<>(buildErrorResponse(ExceptionMessages.invalidDate(dtex.getParsedString())), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity
                .internalServerError()
                .body(buildErrorResponse(OutputMessages.System.UNEXPECTED_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(buildErrorResponse(OutputMessages.System.UNEXPECTED_SERVER_ERROR));
    }

    private static <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}
