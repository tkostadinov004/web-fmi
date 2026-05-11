package bg.sofia.uni.fmi.issuetracker.service.mapper;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProjectFromDTO(UpdateProjectDTO dto, @MappingTarget Project project);
}
