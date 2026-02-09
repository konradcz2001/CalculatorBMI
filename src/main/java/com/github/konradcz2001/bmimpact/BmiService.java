package com.github.konradcz2001.bmimpact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service layer responsible for business logic regarding BMI calculations,
 * unit conversions, and data persistence.
 */
@Service
public class BmiService {

    @Autowired
    private BmiResultRepository bmiResultRepository;

    /**
     * Retrieves all stored BMI results history.
     *
     * @return list of BmiResult
     */
    public List<BmiResult> getAllResults() {
        return bmiResultRepository.findAll();
    }

    /**
     * Processes the BMI form, performs calculations/conversions, determines the category,
     * and saves the result to the database.
     *
     * @param bmiForm the submitted form data
     */
    public void calculateAndSave(BmiForm bmiForm) {
        double heightInCm = bmiForm.getHeight();
        double weightInKg = bmiForm.getWeight();

        // Convert Imperial to Metric if necessary
        if (bmiForm.getUnitSystem() == UnitSystem.IMPERIAL) {
            heightInCm = convertInchesToCm(bmiForm.getHeight());
            weightInKg = convertLbsToKg(bmiForm.getWeight());
        }

        double bmi = calculateBmiValue(heightInCm, weightInKg);
        BmiCategory category = determineCategory(bmi);

        BmiResult bmiResult = new BmiResult();
        bmiResult.setName(bmiForm.getName());
        bmiResult.setHeight((int) Math.round(heightInCm));
        bmiResult.setWeight((int) Math.round(weightInKg));
        bmiResult.setBmi(bmi);
        bmiResult.setCategory(category);
        bmiResult.setTimestamp(new Date());

        bmiResultRepository.save(bmiResult);
    }

    private double calculateBmiValue(double heightCm, double weightKg) {
        if (heightCm == 0) return 0;
        return (double) Math.round((weightKg / (heightCm * heightCm * 0.0001)) * 100) / 100;
    }

    private BmiCategory determineCategory(double bmi) {
        if (bmi < 18.5) {
            return BmiCategory.UNDERWEIGHT;
        } else if (bmi < 25) {
            return BmiCategory.NORMAL;
        } else if (bmi < 30) {
            return BmiCategory.OVERWEIGHT;
        } else {
            return BmiCategory.OBESE;
        }
    }

    private double convertInchesToCm(int inches) {
        return inches * 2.54;
    }

    private double convertLbsToKg(int lbs) {
        return lbs * 0.45359237;
    }
}