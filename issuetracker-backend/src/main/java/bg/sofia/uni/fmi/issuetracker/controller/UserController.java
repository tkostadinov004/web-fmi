package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.PaginationLinkHeader;
import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.input.UpdateUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.exception.file.InvalidFileFormatException;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.InvalidOrExpiredTokenErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuditLogService;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.CommonUtils;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Endpoints for user profile and account management")
public class UserController {
    private final UserService userService;
    private final AuditLogService auditLogService;

    public UserController(UserService userService, AuditLogService auditLogService) {
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @Operation(summary = "List all users", description = "Returns a paginated list of users for admin review.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdminOnlyOutputUserDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<AdminOnlyOutputUserDTO>> getAllUsers(@Parameter(description = "Page number to return, starting at 1", required = false) @RequestParam(name = "page_number", required = false, defaultValue = Constants.DEFAULT_PAGE_NUMBER) Integer pageNumber,
                                                                    @Parameter(description = "Page size to return", required = false) @RequestParam(name = "page_size", required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) Integer pageSize,
                                                                    @Parameter(description = "Field name to sort by", required = false) @RequestParam(name = "order_by", required = false, defaultValue = "username") String orderBy,
                                                                    @Parameter(description = "Sort ascending when true, descending when false", required = false) @RequestParam(name = "asc", required = false, defaultValue = "true") Boolean ascending,
                                                                    HttpServletRequest request) {
        Page<AdminOnlyOutputUserDTO> resultPage = userService.getAllUsers(pageNumber, pageSize, orderBy, ascending);
        PaginationLinkHeader linkHeader = new PaginationLinkHeader(resultPage, request.getRequestURL().toString());

        return ResponseEntity
                .ok()
                .header("Link", linkHeader.toString())
                .body(resultPage.toList());
    }

    @Operation(summary = "Delete a user", description = "Marks the specified user as deleted. This is an admin-only operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found or already deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{username}")
    @RequireAdmin
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Username of the user to delete", required = true) @PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a user's profile picture", description = "Uploads or replaces the profile picture for the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile picture updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid picture file format, picture file larger than limit, or missing picture file",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/profilePictures")
    public ResponseEntity<Void> changeProfilePicture(@Parameter(description = "Multipart file containing the new profile picture", required = true) @RequestParam("file") MultipartFile picture) {
        if (!CommonUtils.isImageFile(picture)) {
            throw new InvalidFileFormatException(ExceptionMessages.File.invalidFormat());
        }

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.setProfilePicture(username, picture);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user details", description = "Retrieves detailed profile information for the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDetailsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@Parameter(description = "Username of the user to retrieve", required = true) @PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @Operation(summary = "Update current user's profile", description = "Updates the profile data of the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid profile update data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "The provided email address in the DTO is already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping
    public ResponseEntity<Void> patchUser(@Valid @RequestBody UpdateUserDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateUser(username, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get audit logs for a user", description = "Retrieves paginated audit log entries related to a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OutputAuditLogDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}/auditLogs")
    public ResponseEntity<List<OutputAuditLogDTO>> getAuditLogs(@Parameter(description = "Username of the user whose audit logs should be returned", required = true) @PathVariable String username,
                                                                @Parameter(description = "Page number to return, starting at 1", required = false) @RequestParam(name = "page_number", required = false, defaultValue = Constants.DEFAULT_PAGE_NUMBER) Integer pageNumber,
                                                                @Parameter(description = "Page size to return", required = false) @RequestParam(name = "page_size", required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) Integer pageSize,
                                                                HttpServletRequest request) {
        Page<OutputAuditLogDTO> page = auditLogService.getAll(username, pageNumber, pageSize);
        PaginationLinkHeader linkHeader = new PaginationLinkHeader(page, request.getRequestURL().toString(), false);
        return ResponseEntity
                .ok()
                .header("Link", linkHeader.toString())
                .body(page.toList());
    }
}