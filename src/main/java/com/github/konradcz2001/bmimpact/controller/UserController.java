package com.github.konradcz2001.bmimpact.controller;

import com.github.konradcz2001.bmimpact.dto.ChangePasswordDto;
import com.github.konradcz2001.bmimpact.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        if (!model.containsAttribute("passwordDto")) {
            model.addAttribute("passwordDto", new ChangePasswordDto());
        }
        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") ChangePasswordDto passwordDto,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordDto", bindingResult);
            redirectAttributes.addFlashAttribute("passwordDto", passwordDto);
            return "redirect:/profile";
        }

        try {
            userService.changePassword(authentication.getName(), passwordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Authentication authentication, HttpServletRequest request) {
        userService.deleteAccount(authentication.getName());

        // Invalidate session and logout
        try {
            request.logout();
        } catch (Exception e) {
            // Ignore logout errors during account deletion redirect
        }

        return "redirect:/login?deleted";
    }
}