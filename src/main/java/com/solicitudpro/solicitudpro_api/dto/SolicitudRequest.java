package com.solicitudpro.solicitudpro_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public class SolicitudRequest {
    
    @Schema(
        description = "Título de la solicitud", 
        example = "Solicitud de ejemplo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String titulo;

    @Schema(
        description = "Descripción de la solicitud", 
        example = "Descripción de la solicitud de ejemplo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 250, message = "La descripción no puede superar los 250 caracteres")
    private String descripcion;

    public SolicitudRequest() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}