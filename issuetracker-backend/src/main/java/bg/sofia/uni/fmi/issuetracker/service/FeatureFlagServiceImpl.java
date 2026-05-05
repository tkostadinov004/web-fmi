package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.UpdateFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import bg.sofia.uni.fmi.issuetracker.repository.FeatureFlagRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.FeatureFlagMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeatureFlagServiceImpl implements FeatureFlagService {
    private final FeatureFlagRepository featureFlagRepository;
    private final FeatureFlagMapper mapper;

    public FeatureFlagServiceImpl(FeatureFlagRepository featureFlagRepository, FeatureFlagMapper mapper) {
        this.featureFlagRepository = featureFlagRepository;
        this.mapper = mapper;
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

        FeatureFlag featureFlag = new FeatureFlag(dto.name(), dto.value());
        featureFlagRepository.save(featureFlag);
    }

    @Override
    public Optional<String> getFeatureFlagValueUnsafe(String name) {
        Optional<FeatureFlag> featureFlag = featureFlagRepository.findById(name);
        if (featureFlag.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(featureFlag.get().getValue());
    }

    @Override
    public String getFeatureFlagValueSafe(String name) {
        Optional<String> featureFlagValue = getFeatureFlagValueUnsafe(name);
        if (featureFlagValue.isEmpty()) {
            throw new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound(name));
        }

        return featureFlagValue.get();
    }

    @Override
    @Transactional
    public void editFeatureFlagValue(String name, UpdateFeatureFlagDTO dto) {
        Optional<FeatureFlag> featureFlag = featureFlagRepository.findById(name);
        if (featureFlag.isEmpty()) {
            throw new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound(name));
        }

        mapper.patchFeatureFlagFromDTO(dto, featureFlag.get());
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
