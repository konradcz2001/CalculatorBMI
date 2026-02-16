package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.BmiForm;
import com.github.konradcz2001.bmimpact.model.BmiCategory;
import com.github.konradcz2001.bmimpact.model.BmiResult;
import com.github.konradcz2001.bmimpact.model.UnitSystem;
import com.github.konradcz2001.bmimpact.repository.BmiResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmiServiceTest {

    @Mock
    private BmiResultRepository bmiResultRepository;

    @InjectMocks
    private BmiService bmiService;

    @Test
    @DisplayName("Should calculate BMI correctly for Metric system")
    void shouldCalculateMetricBmi() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(180); // int
        form.setWeight(75);  // int
        form.setUnitSystem(UnitSystem.METRIC);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        // 75 / (1.8 * 1.8) = 23.148...
        assertThat(result.getBmi()).isCloseTo(23.15, org.assertj.core.data.Offset.offset(0.01));
        assertThat(result.getCategory()).isEqualTo(BmiCategory.NORMAL);
    }

    @Test
    @DisplayName("Should calculate BMI correctly for Imperial system")
    void shouldCalculateImperialBmi() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(70);  // inches (int)
        form.setWeight(160); // lbs (int)
        form.setUnitSystem(UnitSystem.IMPERIAL);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        // (160 / (70 * 70)) * 703 = 22.955...
        assertThat(result.getBmi()).isCloseTo(22.96, org.assertj.core.data.Offset.offset(0.01));
        assertThat(result.getCategory()).isEqualTo(BmiCategory.NORMAL);
    }

    @Test
    @DisplayName("Should correctly categorize BMI as Obese")
    void shouldCategorizeObese() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(180);
        form.setWeight(100); // BMI ~30.86
        form.setUnitSystem(UnitSystem.METRIC);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        assertThat(result.getCategory()).isEqualTo(BmiCategory.OBESE);
    }

    @Test
    @DisplayName("Should save BMI result with username")
    void shouldSaveBmiResult() {
        // Given
        String username = "testUser";
        BmiResult result = new BmiResult();

        // When
        bmiService.saveResult(result, username);

        // Then
        verify(bmiResultRepository, times(1)).save(result);
        assertThat(result.getUsername()).isEqualTo(username);
    }
}