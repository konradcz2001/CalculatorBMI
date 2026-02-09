package com.github.konradcz2001.bmimpact;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

/**
 * Controller handling web requests for the BMI Calculator.
 */
@Controller
public class BmiController {

    @Autowired
    private BmiResultRepository bmiResultRepository;

    /**
     * Displays the main page with the form and history of results.
     *
     * @param model the Spring MVC model
     * @return the name of the view template
     */
    @GetMapping("/")
    public String index(Model model) {
        List<BmiResult> bmiResults = bmiResultRepository.findAll();
        model.addAttribute("bmiResults", bmiResults);
        model.addAttribute("bmiForm", new BmiForm());
        return "index";
    }

    /**
     * Handles the form submission for BMI calculation.
     * Validates input, converts units if necessary, calculates BMI, and saves the result.
     *
     * @param bmiForm       the form data object
     * @param bindingResult holds validation errors
     * @return redirect to index on success, or index view on error
     */
    @PostMapping("/calculate")
    public String calculateBmi(@Valid @ModelAttribute("bmiForm") BmiForm bmiForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        double heightInCm = bmiForm.getHeight();
        double weightInKg = bmiForm.getWeight();

        // Convert Imperial to Metric if necessary
        if (bmiForm.getUnitSystem() == UnitSystem.IMPERIAL) {
            heightInCm = convertInchesToCm(bmiForm.getHeight());
            weightInKg = convertLbsToKg(bmiForm.getWeight());
        }

        double bmi = calculateBmiValue(heightInCm, weightInKg);

        BmiResult bmiResult = new BmiResult();
        bmiResult.setName(bmiForm.getName());
        // Save normalized metric values to database
        bmiResult.setHeight((int) Math.round(heightInCm));
        bmiResult.setWeight((int) Math.round(weightInKg));
        bmiResult.setBmi(bmi);
        bmiResult.setTimestamp(new Date());

        bmiResultRepository.save(bmiResult);

        return "redirect:/";
    }

    /**
     * Helper method to calculate BMI value based on Metric units.
     * Formula: weight (kg) / [height (m)]^2
     *
     * @param heightCm height in centimeters
     * @param weightKg weight in kilograms
     * @return calculated BMI rounded to two decimal places
     */
    private double calculateBmiValue(double heightCm, double weightKg) {
        if (heightCm == 0) return 0;
        return (double) Math.round((weightKg / (heightCm * heightCm * 0.0001)) * 100) / 100;
    }

    private double convertInchesToCm(int inches) {
        return inches * 2.54;
    }

    private double convertLbsToKg(int lbs) {
        return lbs * 0.45359237;
    }
}