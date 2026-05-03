package bg.sofia.uni.fmi.issuetracker.dto.output;

public record AdminOnlyOutputUserDTO(String username,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     boolean isAdmin,
                                     boolean isDeleted) {
}
