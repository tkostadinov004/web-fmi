package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.UpdateFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for managing feature flags.
 */
public interface FeatureFlagService {
    /**
     * Retrieves all configured feature flags.
     *
     * @return a list of {@link OutputFeatureFlagDTO} objects representing all feature flags
     */
    List<OutputFeatureFlagDTO> getAll();

    /**
     * Creates a new feature flag.
     *
     * @param dto the {@link AddFeatureFlagDTO} containing feature flag data
     * @throws FeatureFlagAlreadyExistsException if a feature flag with the given name already exists
     */
    void addFeatureFlag(AddFeatureFlagDTO dto);

    /**
     * Retrieves the value of a feature flag by name without throwing an exception.
     *
     * @param name the name of the feature flag
     * @return an {@link Optional} containing the feature flag value if found, or an empty optional otherwise
     */
    Optional<String> getFeatureFlagValueUnsafe(String name);

    /**
     * Retrieves the value of a feature flag by name.
     *
     * @param name the name of the feature flag
     * @return the value of the feature flag
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    String getFeatureFlagValueSafe(String name);

    /**
     * Updates the value of an existing feature flag.
     *
     * @param name the name of the feature flag to update
     * @param dto  the {@link UpdateFeatureFlagDTO} containing the new value
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    void editFeatureFlagValue(String name, UpdateFeatureFlagDTO dto);

    /**
     * Deletes a feature flag.
     *
     * @param name the name of the feature flag to delete
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    void deleteFeatureFlag(String name);
}
