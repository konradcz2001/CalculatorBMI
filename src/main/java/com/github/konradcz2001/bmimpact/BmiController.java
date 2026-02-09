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
     * Validates input, calculates BMI, and saves the result.
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

        int height = bmiForm.getHeight();
        int weight = bmiForm.getWeight();
        double bmi = calculateBmiValue(height, weight);

        BmiResult bmiResult = new BmiResult();
        bmiResult.setName(bmiForm.getName());
        bmiResult.setHeight(height);
        bmiResult.setWeight(weight);
        bmiResult.setBmi(bmi);
        bmiResult.setTimestamp(new Date());

        bmiResultRepository.save(bmiResult);

        return "redirect:/";
    }

    /**
     * Helper method to calculate BMI value.
     * Formula: weight (kg) / [height (m)]^2
     *
     * @param height height in centimeters
     * @param weight weight in kilograms
     * @return calculated BMI rounded to two decimal places
     */
    private double calculateBmiValue(int height, int weight) {
        return (double) Math.round(((double) weight / (height * height * 0.0001)) * 100) / 100;
    }
}