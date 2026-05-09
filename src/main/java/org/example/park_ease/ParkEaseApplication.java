package org.example.park_ease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ParkEaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkEaseApplication.class, args);
    }

}
