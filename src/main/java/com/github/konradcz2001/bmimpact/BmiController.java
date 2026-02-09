package com.github.konradcz2001.bmimpact;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Controller handling web requests for the BMI Calculator.
 * Delegates business logic to BmiService.
 */
@Controller
public class BmiController {

    @Autowired
    private BmiService bmiService;

    /**
     * Displays the main page with the form and history of results.
     *
     * @param model the Spring MVC model
     * @return the name of the view template
     */
    @GetMapping("/")
    public String index(Model model) {
        List<BmiResult> bmiResults = bmiService.getAllResults();
        model.addAttribute("bmiResults", bmiResults);
        model.addAttribute("bmiForm", new BmiForm());
        return "index";
    }

    /**
     * Handles the form submission for BMI calculation.
     *
     * @param bmiForm       the form data object
     * @param bindingResult holds validation errors
     * @return redirect to index on success, or index view on error
     */
    @PostMapping("/calculate")
    public String calculateBmi(@Valid @ModelAttribute("bmiForm") BmiForm bmiForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Need to reload history if we return to the view with errors
            return "index";
        }

        bmiService.calculateAndSave(bmiForm);

        return "redirect:/";
    }
}