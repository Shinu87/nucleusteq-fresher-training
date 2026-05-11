package com.capstone.interviewtracker.config;

import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.SkillRepository;
import com.capstone.interviewtracker.repository.UserRepository;

import java.util.List;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(UserRepository userRepo,
            SkillRepository skillRepo,
            PasswordEncoder encoder,
            DataSource dataSource) {
        return args -> {

            try (var conn = dataSource.getConnection();
                    var stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ALTER COLUMN password DROP NOT NULL");
                logger.info("Schema fix applied: users.password is now nullable");
            } catch (Exception e) {
                logger.warn("Schema fix skipped or failed (often safe): {}", e.getMessage());
            }

            if (userRepo.findByEmail("hr@company.com").isEmpty()) {
                User hr = new User();
                hr.setName("Admin HR");
                hr.setEmail("hr@company.com");
                hr.setPassword(encoder.encode("Hr@12345"));
                hr.setRole(Role.HR);
                hr.setEnabled(true);
                userRepo.save(hr);
                logger.info("Default HR user created: hr@company.com / Hr@12345");
            }

            if (skillRepo.count() == 0) {
                List<String> defaultSkills = List.of(
                        "Java", "Spring Boot", "Hibernate", "JPA",
                        "REST API", "Microservices", "JavaScript",
                        "TypeScript", "React", "Angular", "Node.js",
                        "Python", "Django", "SQL", "PostgreSQL",
                        "MySQL", "MongoDB", "Kafka", "Docker",
                        "Kubernetes", "AWS", "Azure", "GCP",
                        "DSA", "System Design", "HTML", "CSS");
                for (String s : defaultSkills) {
                    skillRepo.save(new Skill(s));
                }
                logger.info("Default skills seeded: {} items", defaultSkills.size());
            }

        };
    }
}