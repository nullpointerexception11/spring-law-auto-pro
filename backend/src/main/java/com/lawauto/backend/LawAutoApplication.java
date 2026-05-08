package com.lawauto.backend;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCaching
@SpringBootApplication
public class LawAutoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawAutoApplication.class, args);
    }
}
