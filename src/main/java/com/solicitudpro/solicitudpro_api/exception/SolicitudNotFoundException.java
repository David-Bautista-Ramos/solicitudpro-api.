package com.solicitudpro.solicitudpro_api.exception;

public class SolicitudNotFoundException extends RuntimeException {

    public SolicitudNotFoundException(Long id) {
        super("Solicitud no encontrada con ID: " + id);
    }
}