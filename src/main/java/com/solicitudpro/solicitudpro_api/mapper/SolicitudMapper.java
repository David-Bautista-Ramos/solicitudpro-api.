package com.solicitudpro.solicitudpro_api.mapper;

import com.solicitudpro.solicitudpro_api.dto.SolicitudRequest;
import com.solicitudpro.solicitudpro_api.dto.SolicitudResponse;
import com.solicitudpro.solicitudpro_api.dto.SolicitudUpdateRequest;
import com.solicitudpro.solicitudpro_api.model.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public Solicitud toEntity(SolicitudRequest request) {

        return new Solicitud(
                request.getTitulo(),
                request.getDescripcion()
        );
    }

    public Solicitud toEntity(SolicitudUpdateRequest request) {

        Solicitud solicitud = new Solicitud();

        solicitud.setTitulo(request.getTitulo());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setEstado(request.getEstado());

        return solicitud;
    }

    public SolicitudResponse toResponse(Solicitud solicitud) {

        return new SolicitudResponse(solicitud);
    }
}