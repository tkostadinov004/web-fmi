package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.PaginationLinkHeader;
import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
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
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @RequireAdmin
    public ResponseEntity<List<AdminOnlyOutputUserDTO>> getAllUsers(@RequestParam(name = "page_number", required = false, defaultValue = Constants.DEFAULT_PAGE_NUMBER) Integer pageNumber,
                                                                    @RequestParam(name = "page_size", required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) Integer pageSize,
                                                                    @RequestParam(name = "order_by", required = false, defaultValue = "username") String orderBy,
                                                                    @RequestParam(name = "asc", required = false, defaultValue = "true") Boolean ascending,
                                                                    HttpServletRequest request) {
        Page<AdminOnlyOutputUserDTO> resultPage = userService.getAllUsers(pageNumber, pageSize, orderBy, ascending);
        PaginationLinkHeader linkHeader = new PaginationLinkHeader(resultPage, request.getRequestURL().toString());

        return ResponseEntity
                .ok()
                .header("Link", linkHeader.toString())
                .body(resultPage.toList());
    }
}
