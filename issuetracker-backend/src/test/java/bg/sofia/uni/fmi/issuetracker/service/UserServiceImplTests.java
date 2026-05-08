package bg.sofia.uni.fmi.issuetracker.service;

/*
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private UserMapper userMapper;

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

    @Test
    public void testDeleteUser_ThrowsOnNonexistentOrDeletedUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);

        User user = new User();
        user.setUsername("user");
        user.setDeleted(true);
        assertThatThrownBy(() -> userService.deleteUser(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testDeleteUser_Correctly() {
        User user = User.UserBuilder.newBuilder().username("user").password("pass").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
        assertFalse(user.isDeleted());

        userService.deleteUser(user.getUsername());

        assertTrue(user.isDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testIsDeleted_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.isDeleted(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testIsDeleted_Correctly() {
        User user = User.UserBuilder.newBuilder().username("user").password("pass").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));

        user.setDeleted(true);
        assertTrue(userService.isDeleted(user.getUsername()));
        user.setDeleted(false);
        assertFalse(userService.isDeleted(user.getUsername()));
    }

    @Test
    public void testSetProfilePicture_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.setProfilePicture(TEST_USER.getUsername(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testSetProfilePicture_Correctly() {
        User user = User.UserBuilder.newBuilder().username("user").password("pass").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
        doReturn(user).when(userRepository).save(user);

        MultipartFile file = mock();
        when(file.getOriginalFilename()).thenReturn("file.weirdExtension");

        Path expectedFilePath = Path.of(user.getUsername(), Constants.DEFAULT_PAGE_NUMBER + ".weirdExtension");
        userService.setProfilePicture(user.getUsername(), file);
        assertEquals(expectedFilePath.toString(), user.getProfilePicturePath());

        verify(fileService, times(1)).saveOrReplaceFile(file, expectedFilePath);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUser_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testGetUser_Correctly() {
        User user = spy(User.UserBuilder.newBuilder().profilePicturePath("pfpPath").username("user").password("pass").profilePicturePath("/user/pfp.png").build());
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
        Set<ProjectUser> projects = Set.of(
                new ProjectUser(TEST_PROJECT, user, Role.TEAM_LEAD),
                new ProjectUser(TEST_PROJECT_2, user, Role.DEVELOPER)
        );
        doReturn(projects).when(user).getProjects();

        UserDetailsDTO expected = new UserDetailsDTO(user.getProfilePicturePath(), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getCompanyName(), user.isAdmin(),
                projects.stream().map(pu -> new OutputUserProjectDTO(pu.getProject().getName(), pu.getProject().getUuid(), pu.getRole())).toList());
        assertEquals(expected, userService.getUser(user.getUsername()));
    }

    @Test
    public void testGetAllUsers_Correctly() {
        int pageNumber = 5;
        int pageSize = 15;
        String sortBy = "email";
        boolean asc = false;

        Page<User> users = mock();
        Page<AdminOnlyOutputUserDTO> dtoPageMock = mock();
        when(users.map(any(Function.class))).thenReturn(dtoPageMock);
        doReturn(users).when(userRepository).findAll(any(Pageable.class));

        userService.getAllUsers(pageNumber, pageSize, sortBy, asc);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.captor();
        verify(userRepository, times(1)).findAll(pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertEquals(pageNumber - 1, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
        assertEquals(sortBy, pageable.getSort().get().findFirst().get().getProperty());
        assertEquals(asc, pageable.getSort().get().findFirst().get().isAscending());

        verify(users, times(1)).map(any(Function.class));
    }

    @Test
    public void testUpdateUser_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(TEST_USER.getUsername(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testUpdateUser_ThrowsIfNewEmailAlreadyExists() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        String email = "alreadyexists@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        UpdateUserDTO dto = new UpdateUserDTO(null, null, email, null);
        assertThatThrownBy(() -> userService.updateUser(TEST_USER.getUsername(), dto))
                .hasMessage(ExceptionMessages.User.emailAlreadyExists(dto.email()))
                .isExactlyInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    public void testUpdateUser_Successfully() {
        User user = User.UserBuilder.newBuilder()
                .email("email@email.com")
                .firstName("firstName")
                .lastName("lastName")
                .companyName("company")
                .profilePicturePath("/user/pfp.png").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UpdateUserDTO dto = new UpdateUserDTO("newName", "newLastName", "newEmail@email.com", "newCompany");
        userService.updateUser(user.getUsername(), dto);

        verify(userMapper, times(1)).patchUserFromDTO(dto, user);
        verify(userRepository, times(1)).save(user);
    }
}
*/