package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;

import java.util.List;

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
     * Checks if a feature flag is enabled.
     *
     * @param name the name of the feature flag
     * @return {@code true} if the feature flag is enabled, {@code false} if the feature flag does not exist or is set to false
     */
    boolean isFeatureEnabled(String name);

    /**
     * Retrieves a feature flag by name.
     *
     * @param name the name of the feature flag
     * @return the {@link OutputFeatureFlagDTO} containing feature flag details
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    OutputFeatureFlagDTO getFeatureFlag(String name);

    /**
     * Updates the value of a feature flag.
     *
     * @param name the name of the feature flag to update
     * @param newValue the new value for the feature flag
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    void setFeatureFlagValue(String name, boolean newValue);

    /**
     * Deletes a feature flag.
     *
     * @param name the name of the feature flag to delete
     * @throws FeatureFlagNotFoundException if the feature flag with the given name does not exist
     */
    void deleteFeatureFlag(String name);
}
