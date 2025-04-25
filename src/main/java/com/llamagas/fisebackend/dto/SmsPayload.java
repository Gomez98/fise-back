package com.llamagas.fisebackend.dto;

import lombok.Data;

@Data
public class SmsPayload {
    private String companyDb;
    private String username;
    private String password;
    private String dni;
    private String cupon;
    private String importe;
    private String descripcion;
    private String telefonoFise;
    private String telefonoUsr;
}

