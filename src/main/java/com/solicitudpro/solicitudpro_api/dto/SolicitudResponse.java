package com.solicitudpro.solicitudpro_api.dto;

import com.solicitudpro.solicitudpro_api.model.EstadoSolicitud;
import com.solicitudpro.solicitudpro_api.model.Solicitud;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "SolicitudResponse",
        description = "Información de una solicitud registrada"
)
public class SolicitudResponse {

    @Schema(
        description = "ID de la solicitud",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Título de la solicitud",
        example = "Solicitud de ejemplo"
    )
    private String titulo;

    @Schema(
        description = "Descripción de la solicitud",
        example = "Descripción de la solicitud de ejemplo"
    )
    private String descripcion;

    @Schema(
        description = "Estado de la solicitud",
        example = "PENDIENTE"
    )
    private EstadoSolicitud estado;

    @Schema(
        description = "Fecha de creación de la solicitud",
        example = "2023-01-01T00:00:00"
    )
    private LocalDateTime fechaCreacion;

    public SolicitudResponse() {
    }

    public SolicitudResponse(
            Long id,
            String titulo,
            String descripcion,
            EstadoSolicitud estado,
            LocalDateTime fechaCreacion) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public SolicitudResponse(Solicitud solicitud) {
        this.id = solicitud.getId();
        this.titulo = solicitud.getTitulo();
        this.descripcion = solicitud.getDescripcion();
        this.estado = solicitud.getEstado();
        this.fechaCreacion = solicitud.getFechaCreacion();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
