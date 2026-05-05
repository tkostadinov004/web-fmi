package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.input.UpdateUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Endpoints for user profile and account management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Delete a user", description = "Marks the specified user as deleted. This is an admin-only operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
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
            @ApiResponse(responseCode = "400", description = "Invalid or missing picture file",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{username}/profilePicture")
    public ResponseEntity<Void> changeProfilePicture(@Parameter(description = "Username of the user to update", required = true) @PathVariable String username,
                                                     @Parameter(description = "Multipart file containing the new profile picture", required = true) @RequestParam("file") MultipartFile picture) {
        userService.setProfilePicture(username, picture);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user details", description = "Retrieves detailed profile information for the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDetailsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
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
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "The provided email address in the DTO is already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{username}")
    public ResponseEntity<Void> patchUser(@Valid @RequestBody UpdateUserDTO dto, @PathVariable String username) {
        userService.updateUser(username, dto);
        return ResponseEntity.noContent().build();
    }
}
