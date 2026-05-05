package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.UpdateFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagService {
    List<OutputFeatureFlagDTO> getAll();

    void addFeatureFlag(AddFeatureFlagDTO dto);

    Optional<String> getFeatureFlagValueUnsafe(String name);

    String getFeatureFlagValueSafe(String name);

    void editFeatureFlagValue(String name, UpdateFeatureFlagDTO dto);

    void deleteFeatureFlag(String name);
}
