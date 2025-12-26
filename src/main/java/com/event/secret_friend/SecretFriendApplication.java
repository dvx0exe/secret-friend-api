package com.event.secret_friend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SecretFriendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecretFriendApplication.class, args);
    }
}