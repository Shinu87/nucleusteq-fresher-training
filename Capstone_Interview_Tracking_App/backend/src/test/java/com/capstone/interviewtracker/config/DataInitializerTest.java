package com.capstone.interviewtracker.config;

import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.SkillRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for DataInitializer.
 */
class DataInitializerTest {

        /**
         * Tests that a new HR user and default skills are created when none exist.
         */
        @Test
        void testInitWhenNewUserAndNoSkills() throws Exception {
                UserRepository userRepo = mock(UserRepository.class);
                SkillRepository skillRepo = mock(SkillRepository.class);
                PasswordEncoder encoder = mock(PasswordEncoder.class);
                DataSource dataSource = mock(DataSource.class);

                Connection connection = mock(Connection.class);
                Statement statement = mock(Statement.class);

                when(dataSource.getConnection()).thenReturn(connection);
                when(connection.createStatement()).thenReturn(statement);

                when(userRepo.findByEmail("hr@company.com"))
                                .thenReturn(Optional.empty());

                when(skillRepo.count()).thenReturn(0L);
                when(encoder.encode(anyString())).thenReturn("encoded-password");

                DataInitializer initializer = new DataInitializer();

                CommandLineRunner runner = initializer.init(
                                userRepo, skillRepo, encoder, dataSource);

                runner.run(new String[] {});

                verify(statement).execute(contains("ALTER TABLE users"));
                verify(userRepo, times(1)).save(any(User.class));
                verify(skillRepo, atLeastOnce()).save(any(Skill.class));
        }

        /**
         * Tests that no inserts happen when the HR user and skills already exist.
         */
        @Test
        void testInitWhenUserExistsAndSkillsExist() throws Exception {
                UserRepository userRepo = mock(UserRepository.class);
                SkillRepository skillRepo = mock(SkillRepository.class);
                PasswordEncoder encoder = mock(PasswordEncoder.class);
                DataSource dataSource = mock(DataSource.class);

                Connection connection = mock(Connection.class);
                Statement statement = mock(Statement.class);

                when(dataSource.getConnection()).thenReturn(connection);
                when(connection.createStatement()).thenReturn(statement);

                when(userRepo.findByEmail("hr@company.com"))
                                .thenReturn(Optional.of(new User()));

                when(skillRepo.count()).thenReturn(10L);

                DataInitializer initializer = new DataInitializer();

                CommandLineRunner runner = initializer.init(
                                userRepo, skillRepo, encoder, dataSource);

                runner.run(new String[] {});

                verify(userRepo, never()).save(any(User.class));
                verify(skillRepo, never()).save(any(Skill.class));
        }

        /**
         * Tests that the initializer does not crash when the database throws an error.
         */
        @Test
        void testInitWhenDbThrowsException() throws Exception {
                UserRepository userRepo = mock(UserRepository.class);
                SkillRepository skillRepo = mock(SkillRepository.class);
                PasswordEncoder encoder = mock(PasswordEncoder.class);
                DataSource dataSource = mock(DataSource.class);

                when(dataSource.getConnection()).thenThrow(new RuntimeException("DB error"));

                when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(new User()));
                when(skillRepo.count()).thenReturn(1L);

                DataInitializer initializer = new DataInitializer();

                CommandLineRunner runner = initializer.init(
                                userRepo, skillRepo, encoder, dataSource);

                runner.run(new String[] {});

                verify(userRepo, never()).save(any());
                verify(skillRepo, never()).save(any());
        }
}