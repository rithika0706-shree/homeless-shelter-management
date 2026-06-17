package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<Map<String, String>>> ping() {
        return ResponseEntity.ok(ApiResponse.ok("Server is running",
                Map.of("status", "UP", "service", "Homeless Shelter Management System")));
    }
}
