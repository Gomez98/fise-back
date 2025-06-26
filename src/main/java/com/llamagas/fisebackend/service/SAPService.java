package com.llamagas.fisebackend.service;

import com.llamagas.fisebackend.dto.SmsPayload;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SAPService {

    private String sessionCookie;

    public void guardarEnUDO(SmsPayload payload) {
        guardarEnUDO(payload, false);
    }

    private void guardarEnUDO(SmsPayload payload, boolean reintentado) {
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            sessionCookie = loginSAP(payload.getUsername(), payload.getPassword(), payload.getCompanyDb());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Cookie", sessionCookie);

        Map<String, Object> body = new HashMap<>();
        String uId = payload.getDni() + "-" + payload.getCupon();
        body.put("Code", uId);
        body.put("Name", uId);
        body.put("U_id", uId);
        body.put("U_fise_numero", payload.getTelefonoFise());
        body.put("U_usr_numero", payload.getTelefonoUsr());
        body.put("U_usr_dni", payload.getDni());
        body.put("U_fise_codigo", payload.getCupon());
        body.put("U_importe", new BigDecimal(payload.getImporte()));
        body.put("U_descripcion", payload.getDescripcion());
        body.put("U_activo", 1);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://10.3.92.138:50000/b1s/v1/LLG_FISE_SMS01",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            // Opcional: validar si quieres confirmar éxito explícitamente
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al guardar en UDO: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.Unauthorized ex) {
            if (!reintentado) {
                sessionCookie = loginSAP(payload.getUsername(), payload.getPassword(), payload.getCompanyDb());
                guardarEnUDO(payload, true);  // Reintentar una vez
            } else {
                throw new RuntimeException("No autorizado tras reintento de login a SAP.", ex);
            }
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Error HTTP al intentar guardar en UDO: " + ex.getStatusCode(), ex);
        }
    }

    private String loginSAP(String username, String password, String companyDb) {
        Map<String, String> body = Map.of(
                "CompanyDB", companyDb,
                "UserName", username,
                "Password", password
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://10.3.92.138:50000/b1s/v1/Login",
                HttpMethod.POST,
                entity,
                String.class
        );

        List<String> cookies = response.getHeaders().get("Set-Cookie");

        if (cookies != null) {
            return cookies.stream()
                    .filter(cookie -> cookie.contains("B1SESSION"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró la cookie B1SESSION."));
        } else {
            throw new RuntimeException("No se obtuvo sesión SAP (Set-Cookie vacío).");
        }
    }
}
 