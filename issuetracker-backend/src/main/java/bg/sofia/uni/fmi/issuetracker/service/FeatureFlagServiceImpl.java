package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import bg.sofia.uni.fmi.issuetracker.repository.FeatureFlagRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeatureFlagServiceImpl implements FeatureFlagService {
    private final FeatureFlagRepository featureFlagRepository;

    public FeatureFlagServiceImpl(FeatureFlagRepository featureFlagRepository) {
        this.featureFlagRepository = featureFlagRepository;
    }

    @Override
    public List<OutputFeatureFlagDTO> getAll() {
        return featureFlagRepository
                .findAll()
                .stream()
                .map(ff -> new OutputFeatureFlagDTO(ff.getName(), ff.getValue()))
                .toList();
    }

    @Override
    public void addFeatureFlag(AddFeatureFlagDTO dto) {
        if (featureFlagRepository.existsById(dto.name())) {
            throw new FeatureFlagAlreadyExistsException(ExceptionMessages.FeatureFlag.featureFlagAlreadyExists(dto.name()));
        }

        FeatureFlag featureFlag = new FeatureFlag(dto.name(), dto.valueAsBoolean());
        featureFlagRepository.save(featureFlag);
    }

    @Override
    public boolean isFeatureEnabled(String name) {
        Optional<FeatureFlag> featureFlag = featureFlagRepository.findById(name);
        return featureFlag.isPresent() && featureFlag.get().getValue();
    }

    @Override
    public OutputFeatureFlagDTO getFeatureFlag(String name) {
        Optional<FeatureFlag> featureFlagOpt = featureFlagRepository.findById(name);
        if (featureFlagOpt.isEmpty()) {
            throw new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound(name));
        }

        FeatureFlag featureFlag = featureFlagOpt.get();
        return new OutputFeatureFlagDTO(featureFlag.getName(), featureFlag.getValue());
    }

    @Override
    @Transactional
    public void setFeatureFlagValue(String name, boolean newValue) {
        Optional<FeatureFlag> featureFlag = featureFlagRepository.findById(name);
        if (featureFlag.isEmpty()) {
            throw new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound(name));
        }

        featureFlag.get().setValue(newValue);
        featureFlagRepository.save(featureFlag.get());
    }

    @Override
    public void deleteFeatureFlag(String name) {
        Optional<FeatureFlag> featureFlag = featureFlagRepository.findById(name);
        if (featureFlag.isEmpty()) {
            throw new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound(name));
        }

        featureFlagRepository.delete(featureFlag.get());
    }
}
