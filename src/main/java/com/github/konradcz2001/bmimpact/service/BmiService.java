package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.BmiForm;
import com.github.konradcz2001.bmimpact.model.BmiCategory;
import com.github.konradcz2001.bmimpact.model.BmiResult;
import com.github.konradcz2001.bmimpact.model.UnitSystem;
import com.github.konradcz2001.bmimpact.repository.BmiResultRepository;
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
     * Retrieves BMI results for a specific user.
     *
     * @param username the username to fetch history for
     * @return list of BmiResult
     */
    public List<BmiResult> getResultsByUser(String username) {
        return bmiResultRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Calculates BMI data based on the form but DOES NOT save it to the database.
     *
     * @param bmiForm the submitted form data
     * @return a transient BmiResult object
     */
    public BmiResult calculate(BmiForm bmiForm) {
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
        bmiResult.setHeight((int) Math.round(heightInCm));
        bmiResult.setWeight((int) Math.round(weightInKg));
        bmiResult.setBmi(bmi);
        bmiResult.setCategory(category);
        bmiResult.setTimestamp(new Date());

        return bmiResult;
    }

    /**
     * Saves the result to the database linked to a user.
     *
     * @param result the BmiResult to save
     * @param username the username of the owner
     */
    public void saveResult(BmiResult result, String username) {
        result.setUsername(username);
        bmiResultRepository.save(result);
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