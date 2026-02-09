package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.BmiForm;
import com.github.konradcz2001.bmimpact.model.BmiCategory;
import com.github.konradcz2001.bmimpact.model.BmiResult;
import com.github.konradcz2001.bmimpact.model.UnitSystem;
import com.github.konradcz2001.bmimpact.repository.BmiResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service layer responsible for business logic regarding BMI calculations,
 * unit conversions, and data persistence.
 */
@Service
public class BmiService {

    @Autowired
    private BmiResultRepository bmiResultRepository;

    /**
     * Retrieves BMI results for a specific user with pagination.
     *
     * @param username the username to fetch history for
     * @param page     page number (0-based)
     * @param size     page size
     * @return Page of BmiResult
     */
    public Page<BmiResult> getResultsByUser(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return bmiResultRepository.findByUsername(username, pageable);
    }

    /**
     * Deletes a specific BMI result if it belongs to the given user.
     *
     * @param id       the ID of the result to delete
     * @param username the username of the requestor
     */
    public void deleteResult(String id, String username) {
        bmiResultRepository.deleteByIdAndUsername(id, username);
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