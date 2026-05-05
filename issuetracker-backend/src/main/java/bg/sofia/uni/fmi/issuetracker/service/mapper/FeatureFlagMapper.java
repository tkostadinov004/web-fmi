package bg.sofia.uni.fmi.issuetracker.service.mapper;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.UpdateFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FeatureFlagMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchFeatureFlagFromDTO(UpdateFeatureFlagDTO dto, @MappingTarget FeatureFlag featureFlag);
}
