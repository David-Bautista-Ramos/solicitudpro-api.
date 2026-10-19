package com.solicitudpro.solicitudpro_api.dto;

import com.solicitudpro.solicitudpro_api.model.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public class SolicitudUpdateRequest {

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
    private String descripcion;

    @Schema(
        description = "Estado de la solicitud",
        example = "PENDIENTE",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "El estado es obligatorio")
    private EstadoSolicitud estado;

    public SolicitudUpdateRequest() {
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

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
}
