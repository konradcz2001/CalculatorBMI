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
        BmiForm form = createForm(180, 75, UnitSystem.METRIC);

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
        BmiForm form = createForm(70, 160, UnitSystem.IMPERIAL);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        // (160 / (70 * 70)) * 703 = 22.955...
        assertThat(result.getBmi()).isCloseTo(22.96, org.assertj.core.data.Offset.offset(0.01));
        assertThat(result.getCategory()).isEqualTo(BmiCategory.NORMAL);
    }

    @Test
    @DisplayName("Boundary check: BMI exactly 25.0 should be Overweight")
    void shouldCategorizeBoundaryOverweight() {
        // Given
        // 81kg / (1.8m * 1.8m) = 25.0
        BmiForm form = createForm(180, 81, UnitSystem.METRIC);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        assertThat(result.getBmi()).isEqualTo(25.0);
        assertThat(result.getCategory()).isEqualTo(BmiCategory.OVERWEIGHT);
    }

    @Test
    @DisplayName("Boundary check: BMI exactly 18.5 should be Normal")
    void shouldCategorizeBoundaryNormal() {
        // Given
        // 60kg / (1.8m * 1.8m) ≈ 18.52
        BmiForm form = createForm(180, 60, UnitSystem.METRIC);

        // When
        BmiResult result = bmiService.calculate(form);

        // Then
        assertThat(result.getBmi()).isGreaterThanOrEqualTo(18.5);
        assertThat(result.getCategory()).isEqualTo(BmiCategory.NORMAL);
    }

    @Test
    @DisplayName("Should correctly categorize BMI as Obese")
    void shouldCategorizeObese() {
        // Given
        BmiForm form = createForm(180, 100, UnitSystem.METRIC);

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

    private BmiForm createForm(int h, int w, UnitSystem system) {
        BmiForm form = new BmiForm();
        form.setHeight(h);
        form.setWeight(w);
        form.setUnitSystem(system);
        return form;
    }
}