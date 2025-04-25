package com.llamagas.fisebackend.controller;

import com.llamagas.fisebackend.dto.SmsPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthController {


    @GetMapping
    public ResponseEntity<String> registrarSMS() {
        return ResponseEntity.ok("Servicios para FISE status: ok");
    }
}
