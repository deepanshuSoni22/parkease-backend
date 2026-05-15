package org.example.park_ease.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> home() {

        return Map.of(
                "app", "ParkEase API",
                "version", "v1",
                "status", "running"
        );
    }

}
