package com.konrad.kalkulatorbmi;

// BmiController.java

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

@Controller
public class BmiController {

    @Autowired
    private BmiResultRepository bmiResultRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<BmiResult> bmiResults = bmiResultRepository.findAll();
        model.addAttribute("bmiResults", bmiResults);
        model.addAttribute("bmiForm", new BmiForm());
        return "index";
    }


    @PostMapping("/calculate")
    public String calculateBmi(@Valid @ModelAttribute("bmiForm") BmiForm bmiForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        // Reszta kodu
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

    private double calculateBmiValue(int height, int weight) {
        // Obliczanie BMI - formuła: BMI = masa ciała / (wzrost * wzrost)
        return  (double)Math.round(((double)weight /  (height * height * 0.0001)) * 100) / 100;
    }
}
