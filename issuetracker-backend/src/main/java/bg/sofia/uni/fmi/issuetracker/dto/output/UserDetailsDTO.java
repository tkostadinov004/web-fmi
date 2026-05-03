package bg.sofia.uni.fmi.issuetracker.dto.output;

import org.springframework.core.io.Resource;

import java.util.List;

public record UserDetailsDTO(Resource profilePicture,
                             String username,
                             String firstName,
                             String lastName,
                             String email,
                             String companyName,
                             boolean isAdmin,
                             List<OutputUserProjectDTO> projects) {
}
