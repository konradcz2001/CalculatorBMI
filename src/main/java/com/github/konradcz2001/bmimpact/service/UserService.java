package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.ChangePasswordDto;
import com.github.konradcz2001.bmimpact.dto.UserRegistrationDto;
import com.github.konradcz2001.bmimpact.model.User;
import com.github.konradcz2001.bmimpact.repository.BmiResultRepository;
import com.github.konradcz2001.bmimpact.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user management operations like registration.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BmiResultRepository bmiResultRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BmiResultRepository bmiResultRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bmiResultRepository = bmiResultRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * @param dto the registration data
     * @throws IllegalArgumentException if username already exists
     */
    public void registerUser(UserRegistrationDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
    }

    public void changePassword(String username, ChangePasswordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(String username) {
        bmiResultRepository.deleteAllByName(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
    }
}