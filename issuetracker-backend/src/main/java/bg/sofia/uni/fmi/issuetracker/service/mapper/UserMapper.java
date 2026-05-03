package bg.sofia.uni.fmi.issuetracker.service.mapper;

import bg.sofia.uni.fmi.issuetracker.dto.input.UpdateUserDTO;
import bg.sofia.uni.fmi.issuetracker.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUserFromDTO(UpdateUserDTO dto, @MappingTarget User user);
}
