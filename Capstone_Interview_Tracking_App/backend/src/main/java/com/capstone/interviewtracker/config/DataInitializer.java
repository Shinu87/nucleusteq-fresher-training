package com.capstone.interviewtracker.config;

import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if (repo.findByEmail("hr@company.com").isEmpty()) {

                User hr = new User();
                hr.setName("Admin HR");
                hr.setEmail("hr@company.com");
                hr.setPassword(encoder.encode("12345"));
                hr.setRole(Role.HR);

                repo.save(hr);

                System.out.println("HR user created successfully");
            }
        };
    }
}