package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import bg.sofia.uni.fmi.issuetracker.repository.FeatureFlagRepository;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.FEATURE_FLAGS;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_FEATURE_FLAG_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeatureFlagServiceImplTests {
    @Mock
    private FeatureFlagRepository featureFlagRepository;

    @InjectMocks
    private FeatureFlagServiceImpl service;

    @Test
    public void testGetAll_Correctly() {
        when(featureFlagRepository.findAll()).thenReturn(FEATURE_FLAGS);

        List<OutputFeatureFlagDTO> expected = FEATURE_FLAGS
                .stream().map(ff -> new OutputFeatureFlagDTO(ff.getName(), ff.getValue()))
                .toList();

        assertIterableEquals(expected, service.getAll());
    }

    @Test
    public void testAddFeatureFlag_ThrowsOnAlreadyExistingFeatureFlag() {
        AddFeatureFlagDTO dto = new AddFeatureFlagDTO("TEST_FEATURE_FLAG_1", "false");
        when(featureFlagRepository.existsById(dto.name())).thenReturn(true);

        assertThatThrownBy(() -> service.addFeatureFlag(dto))
                .hasMessage(ExceptionMessages.FeatureFlag.featureFlagAlreadyExists(dto.name()))
                .isExactlyInstanceOf(FeatureFlagAlreadyExistsException.class);
    }

    @Test
    public void testAddFeatureFlag_Successfully() {
        AddFeatureFlagDTO dto = new AddFeatureFlagDTO("TEST_FEATURE_FLAG_1", "false");
        when(featureFlagRepository.existsById(dto.name())).thenReturn(false);
        doAnswer(a -> null).when(featureFlagRepository).save(any());

        service.addFeatureFlag(dto);

        ArgumentCaptor<FeatureFlag> argumentCaptor = ArgumentCaptor.captor();
        verify(featureFlagRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getName(), dto.name());
        assertEquals(argumentCaptor.getValue().getValue(), dto.valueAsBoolean());
    }

    @Test
    public void testIsFeatureEnabled_ReturnsFalseIfFeatureFlagIsNotPresent() {
        when(featureFlagRepository.findById("test")).thenReturn(Optional.empty());

        assertFalse(service.isFeatureEnabled("test"));
    }

    @Test
    public void testIsFeatureEnabled_ReturnsFalseIfFeatureFlagIsPresentAndSetToFalse() {
        FeatureFlag falseFlag = new FeatureFlag("flag", false);
        when(featureFlagRepository.findById(falseFlag.getName())).thenReturn(Optional.of(falseFlag));

        assertFalse(service.isFeatureEnabled(falseFlag.getName()));
    }

    @Test
    public void testIsFeatureEnabled_ReturnsTrueIfFeatureFlagIsPresentAndSetToTrue() {
        FeatureFlag falseFlag = new FeatureFlag("flag", true);
        when(featureFlagRepository.findById(falseFlag.getName())).thenReturn(Optional.of(falseFlag));

        assertTrue(service.isFeatureEnabled(falseFlag.getName()));
    }

    @Test
    public void testGetFeatureFlag_ThrowsOnNonexistentFeatureFlag() {
        when(featureFlagRepository.findById(TEST_FEATURE_FLAG_1.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFeatureFlag(TEST_FEATURE_FLAG_1.getName()))
                .hasMessage(ExceptionMessages.FeatureFlag.featureFlagNotFound(TEST_FEATURE_FLAG_1.getName()))
                .isExactlyInstanceOf(FeatureFlagNotFoundException.class);
    }

    @Test
    public void testGetFeatureFlag_Correctly() {
        when(featureFlagRepository.findById(TEST_FEATURE_FLAG_1.getName())).thenReturn(Optional.of(TEST_FEATURE_FLAG_1));

        OutputFeatureFlagDTO expected = new OutputFeatureFlagDTO(TEST_FEATURE_FLAG_1.getName(), TEST_FEATURE_FLAG_1.getValue());
        assertEquals(expected, service.getFeatureFlag(TEST_FEATURE_FLAG_1.getName()));
    }

    @Test
    public void testSetFeatureFlagValue_ThrowsOnNonexistentFeatureFlag() {
        when(featureFlagRepository.findById(TEST_FEATURE_FLAG_1.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setFeatureFlagValue(TEST_FEATURE_FLAG_1.getName(), false))
                .hasMessage(ExceptionMessages.FeatureFlag.featureFlagNotFound(TEST_FEATURE_FLAG_1.getName()))
                .isExactlyInstanceOf(FeatureFlagNotFoundException.class);
    }

    @Test
    public void testSetFeatureFlagValue_Correctly() {
        FeatureFlag flag = new FeatureFlag("test", false);
        when(featureFlagRepository.findById(flag.getName())).thenReturn(Optional.of(flag));
        doAnswer(a -> null).when(featureFlagRepository).save(any());

        service.setFeatureFlagValue(flag.getName(), true);
        assertTrue(flag.getValue());

        verify(featureFlagRepository, times(1)).save(flag);
    }

    @Test
    public void testDeleteFeatureFlag_ThrowsOnNonexistentFeatureFlag() {
        when(featureFlagRepository.findById(TEST_FEATURE_FLAG_1.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteFeatureFlag(TEST_FEATURE_FLAG_1.getName()))
                .hasMessage(ExceptionMessages.FeatureFlag.featureFlagNotFound(TEST_FEATURE_FLAG_1.getName()))
                .isExactlyInstanceOf(FeatureFlagNotFoundException.class);
    }

    @Test
    public void testDeleteFeatureFlagValue_Correctly() {
        when(featureFlagRepository.findById(TEST_FEATURE_FLAG_1.getName())).thenReturn(Optional.of(TEST_FEATURE_FLAG_1));

        service.deleteFeatureFlag(TEST_FEATURE_FLAG_1.getName());

        verify(featureFlagRepository, times(1)).delete(TEST_FEATURE_FLAG_1);
    }
}
