package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.PaginationLinkHeader;
import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Validated
@Tag(name = "Admin", description = "Operations for admin actions")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users", description = "Returns a paginated list of users for admin review.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdminOnlyOutputUserDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users")
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
}
