package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterDTO user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDTO user) {
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changePassword(username, changePasswordDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Void> sendForgotPasswordEmail(@Valid @RequestBody SendForgotPasswordEmailDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.sendForgotPasswordEmail(username, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/forgotPassword")
    public ResponseEntity<Void> changeForgottenPassword(@Valid @RequestBody ChangeForgottenPasswordDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changeForgottenPassword(username, dto);
        return ResponseEntity.noContent().build();
    }
}
