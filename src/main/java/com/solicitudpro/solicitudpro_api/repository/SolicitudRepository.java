
package com.solicitudpro.solicitudpro_api.repository;

import com.solicitudpro.solicitudpro_api.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository
        extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findAllByPropietarioId(Long propietarioId);

    Optional<Solicitud> findByIdAndPropietarioId(
            Long id,
            Long propietarioId
    );
}