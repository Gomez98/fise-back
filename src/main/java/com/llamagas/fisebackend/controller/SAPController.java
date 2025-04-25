package com.llamagas.fisebackend.controller;

import com.llamagas.fisebackend.dto.SmsPayload;
import com.llamagas.fisebackend.service.SAPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sap")
@RequiredArgsConstructor
public class SAPController {

    private final SAPService sapService;

    @PostMapping("/registrar-sms")
    public ResponseEntity<String> registrarSMS(@RequestBody SmsPayload payload) {
        sapService.guardarEnUDO(payload);
        return ResponseEntity.ok("Enviado a SAP correctamente");
    }
}

