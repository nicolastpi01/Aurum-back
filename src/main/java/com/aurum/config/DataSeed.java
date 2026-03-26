package com.aurum.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.aurum.accounts.domain.Account;
import com.aurum.accounts.repository.AccountRepository;
import com.aurum.users.domain.Role;
import com.aurum.users.domain.User;
import com.aurum.users.repository.UserRepository;

@Configuration
@Profile("dev") // Solo se ejecuta en perfil dev
public class DataSeed {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                   AccountRepository accountRepository, 
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "nico_dev@aurum.com";

            if (userRepository.findByMail(email).isEmpty()) {
                // 1. Creamos el Usuario de prueba para el MVP
                User user = new User();
                user.setMail(email);
                // Importante: Usamos el encoder para que coincida con el login
                user.setPassword_hash(passwordEncoder.encode("Password123!"));
                user.setRole(Role.USER);
                userRepository.save(user);

                // 2. Creamos la Cuenta asociada para que el Dashboard no esté vacío
                Account account = new Account();
                account.setUser(user);
                account.setBalance(7500.00);
                account.setCurrency("ARS");
                account.setStatus("ACTIVE");
                accountRepository.save(account);

                System.out.println("🌱 DataSeed: Usuario '" + email + "' y cuenta de prueba creados en H2.");
            } else {
                System.out.println("✅ DataSeed: El usuario de prueba ya existe, saltando creación.");
            }
        };
    }
}