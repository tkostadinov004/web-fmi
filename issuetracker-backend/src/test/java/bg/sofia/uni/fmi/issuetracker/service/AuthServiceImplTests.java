package bg.sofia.uni.fmi.issuetracker.service;

/*
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTests {
    private static final UserRegisterDTO REGISTER_USER =
            new UserRegisterDTO("FirstName", "LastName", "user", "email@email.com", "company", "pass");
    private static final UserLoginDTO LOGIN_USER =
            new UserLoginDTO("user", "pass");

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testRegister_ThrowsWhenUserAlreadyExists() {
        when(userRepository.existsById(REGISTER_USER.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(REGISTER_USER))
                .isExactlyInstanceOf(UserAlreadyExistsException.class)
                .hasMessage(ExceptionMessages.User.userAlreadyExists(REGISTER_USER.username()));
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsById(REGISTER_USER.username())).thenReturn(false);
        doReturn("encodedPass").when(passwordEncoder).encode(any());
        doAnswer(a -> null).when(userRepository).save(any());

        AuthResponse result = authService.register(REGISTER_USER);
        assertEquals(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER, result.message());
        assertNull(result.token());
        verify(passwordEncoder, times(1)).encode(REGISTER_USER.password());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(REGISTER_USER.firstName(), captor.getValue().getFirstName());
        assertEquals(REGISTER_USER.lastName(), captor.getValue().getLastName());
        assertEquals(REGISTER_USER.username(), captor.getValue().getUsername());
        assertEquals("encodedPass", captor.getValue().getPassword());
    }

    @Test
    void testLogin_ThrowsOnNonexistentUser() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.wrongCredentials())
                .isExactlyInstanceOf(WrongCredentialsException.class);
    }

    @Test
    void testLogin_ThrowsOnWrongCredentials() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(false).when(passwordEncoder).matches(any(), any());

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.wrongCredentials())
                .isExactlyInstanceOf(WrongCredentialsException.class);

        verify(passwordEncoder, times(1)).matches(LOGIN_USER.password(), TEST_USER.getPassword());
    }

    @Test
    void testLogin_ThrowsWhenUserIsAlreadyLoggedIn() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(true).when(passwordEncoder).matches(any(), any());

        Token token = new Token("testToken", TEST_USER);
        when(tokenRepository.findAllByUser(TEST_USER)).thenReturn(List.of(token));
        when(jwtUtils.isTokenExpired(token.getTokenValue())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.userAlreadyLoggedIn(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserAlreadyLoggedException.class);

        verify(jwtUtils, times(1)).isTokenExpired(token.getTokenValue());
    }

    @Test
    void testLogin_Successfully() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(true).when(passwordEncoder).matches(any(), any());

        Token oldToken = new Token("oldToken", TEST_USER);
        when(jwtUtils.isTokenExpired(oldToken.getTokenValue())).thenReturn(true);
        when(tokenRepository.findAllByUser(TEST_USER)).thenReturn(List.of(oldToken));

        Token token = new Token("testToken", TEST_USER);
        doReturn(token).when(authService).createToken(TEST_USER);

        AuthResponse response = authService.login(LOGIN_USER);
        assertEquals(OutputMessages.Auth.SUCCESSFULLY_LOGGED_USER, response.message());
        assertEquals(token.getTokenValue(), response.token());

        verify(jwtUtils, times(1)).isTokenExpired(any());
        verify(tokenRepository, times(1)).deleteAll(any());
    }

    @Test
    void testLogout_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testLogout_Success() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        authService.logout(TEST_USER.getUsername());

        verify(tokenRepository, times(1)).deleteAllByUser(TEST_USER);
    }

    @Test
    void testCreateToken_Success() {
        Token token = new Token("testToken", TEST_USER);
        when(jwtUtils.generateToken(TEST_USER, Constants.DEFAULT_AUTH_TOKEN_VALIDITY)).thenReturn(token.getTokenValue());
        when(tokenRepository.save(token)).thenReturn(token);

        Token actual = authService.createToken(TEST_USER);
        assertEquals(token, actual);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        assertEquals("testToken", tokenCaptor.getValue().getTokenValue());
        assertEquals(TEST_USER, tokenCaptor.getValue().getUser());
    }
}
*/