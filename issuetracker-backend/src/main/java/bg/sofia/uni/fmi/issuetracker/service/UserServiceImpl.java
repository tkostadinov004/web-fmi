package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputUserProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectUserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.FileService;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final ProjectUserRepository projectUserRepository;

    public UserServiceImpl(UserRepository userRepository, FileService fileService, ProjectUserRepository projectUserRepository) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.projectUserRepository = projectUserRepository;
    }

    @Override
    public boolean isAdmin(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        return user.get().isAdmin();
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty() || user.get().isDeleted()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        user.get().setDeleted(true);
        userRepository.save(user.get());
    }

    @Override
    public boolean isDeleted(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        return user.get().isDeleted();
    }

    @Override
    @Transactional
    public void setProfilePicture(String username, MultipartFile picture) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        String extension = FilenameUtils.getExtension(picture.getOriginalFilename());
        Path fileOutputPath = Path.of(username, Constants.USER_PROFILE_PICTURE_FILENAME + "." + extension);
        fileService.saveOrReplaceFile(picture, fileOutputPath);

        user.get().setProfilePicturePath(fileOutputPath.toString());
        userRepository.save(user.get());
    }

    @Override
    public UserDetailsDTO getUser(String username) {
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        User user = userOpt.get();
        Resource profilePicture = user.getProfilePicturePath() != null ? fileService.getFile(user.getProfilePicturePath()) : null;
        List<OutputUserProjectDTO> projects = projectUserRepository
                .findAllByUser(user)
                .stream()
                .map(pu -> new OutputUserProjectDTO(pu.getProject().getName(), pu.getProject().getUuid()))
                .toList();

        return new UserDetailsDTO(profilePicture, user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getCompanyName(), user.isAdmin(), projects);
    }

    @Override
    public Page<AdminOnlyOutputUserDTO> getAllUsers(int pageNumber, int pageSize, String orderBy, boolean ascending) {
        pageNumber = pageNumber <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_NUMBER) : pageNumber;
        pageSize = pageSize <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_SIZE) : pageSize;

        Sort sort = Sort.by(orderBy);
        if (!ascending) {
            sort = sort.descending();
        }
        Pageable pageRequest = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<User> page = userRepository.findAll(pageRequest);

        return page
                .map(u -> new AdminOnlyOutputUserDTO(u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail(), u.isAdmin(), u.isDeleted()));
    }
}
