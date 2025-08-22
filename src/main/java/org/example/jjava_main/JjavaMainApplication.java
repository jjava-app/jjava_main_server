package org.example.jjava_main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JjavaMainApplication {

    public static void main(String[] args) {
//        Dotenv dotenv = Dotenv.load();
//        dotenv.entries().forEach(entry ->
//                System.setProperty(entry.getKey(), entry.getValue())
//        );
        SpringApplication.run(JjavaMainApplication.class, args);
    }
}
