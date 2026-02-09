package com.github.konradcz2001.bmimpact.controller;

import com.github.konradcz2001.bmimpact.dto.BmiForm;
import com.github.konradcz2001.bmimpact.model.BmiResult;
import com.github.konradcz2001.bmimpact.service.BmiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
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
     * Displays the main page.
     * Fetches history only if the user is logged in.
     */
    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        populateModel(model, authentication);

        // If "bmiForm" is not already in the model (e.g. from flash attributes), add a new one
        if (!model.containsAttribute("bmiForm")) {
            model.addAttribute("bmiForm", new BmiForm());
        }

        return "index";
    }

    /**
     * Handles the calculation.
     * - Calculates BMI for everyone.
     * - Saves to DB ONLY if logged in.
     * - Uses Flash Attributes to persist the result across the redirect.
     */
    @PostMapping("/calculate")
    public String calculateBmi(@Valid @ModelAttribute("bmiForm") BmiForm bmiForm,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model, // Added Model to populate history on error
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Re-populate history so the table doesn't disappear
            populateModel(model, authentication);
            return "index";
        }

        // 1. Calculate
        BmiResult result = bmiService.calculate(bmiForm);

        // 2. Save only if authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            bmiService.saveResult(result, authentication.getName());
        }

        // 3. Pass result to the view (Redirect - PRG Pattern)
        redirectAttributes.addFlashAttribute("calculatedResult", result);
        redirectAttributes.addFlashAttribute("bmiForm", new BmiForm()); // Reset form

        return "redirect:/";
    }

    /**
     * Helper method to fetch and add BMI history to the model based on authentication status.
     *
     * @param model the Spring MVC model
     * @param authentication the current security context
     */
    private void populateModel(Model model, Authentication authentication) {
        List<BmiResult> bmiResults = Collections.emptyList();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            bmiResults = bmiService.getResultsByUser(username);
        }

        model.addAttribute("bmiResults", bmiResults);
    }
}