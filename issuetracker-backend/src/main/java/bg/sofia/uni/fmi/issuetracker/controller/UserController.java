package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/{username}")
    @RequireAdmin
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}/profilePicture")
    public ResponseEntity<Void> changeProfilePicture(@PathVariable String username,
                                                     @RequestParam("file") MultipartFile picture) {
        userService.setProfilePicture(username, picture);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }
}
