package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testIsAdmin_ThrowsOnNonexistentUser() {
        String username = "testUsername";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.isAdmin(username))
                .hasMessage(ExceptionMessages.User.userNotFound(username))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testIsAdmin_ReturnsCorrectly() {
        User spy = spy(TEST_USER);
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(spy));

        userService.isAdmin(TEST_USER.getUsername());
        verify(spy, times(1)).isAdmin();
    }
}
