package com.onlinejobportal;

import com.onlinejobportal.entity.Role;
import com.onlinejobportal.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class JobportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobportalApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            String[] roleNames = {"ROLE_ADMIN", "ROLE_EMPLOYER", "ROLE_JOBSEEKER"};
            for (String roleName : roleNames) {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    roleRepository.save(role);
                    log.info("Seeded role: {}", roleName);
                } else {
                    log.debug("Role already exists: {}", roleName);
                }
            }
            log.info("Role seeding completed successfully.");
        };
    }

}

