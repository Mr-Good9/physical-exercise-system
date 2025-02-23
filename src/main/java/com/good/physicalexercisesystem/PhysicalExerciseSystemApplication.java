package com.good.physicalexercisesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PhysicalExerciseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhysicalExerciseSystemApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, the server is running!";
    }
}
