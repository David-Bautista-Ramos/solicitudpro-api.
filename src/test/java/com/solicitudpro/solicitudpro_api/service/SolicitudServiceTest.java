package com.solicitudpro.solicitudpro_api.service;

import com.solicitudpro.solicitudpro_api.exception.SolicitudNotFoundException;
import com.solicitudpro.solicitudpro_api.model.Solicitud;
import com.solicitudpro.solicitudpro_api.repository.SolicitudRepository;
import com.solicitudpro.solicitudpro_api.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudRepository solicitudRepository;

    @InjectMocks
    private SolicitudService solicitudService;

    @Test
    void debeObtenerTodasLasSolicitudes() {

        // Arrange
        Solicitud solicitud = new Solicitud(
                "Solicitud de prueba",
                "Descripción de prueba"
        );

        when(solicitudRepository.findAll())
                .thenReturn(List.of(solicitud));

        // Act
        List<Solicitud> resultado =
                solicitudService.obtenerTodas();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(
                "Solicitud de prueba",
                resultado.get(0).getTitulo()
        );
    }

    @Test
        void debeObtenerSolicitudPorId() {

        // Arrange
        Solicitud solicitud = new Solicitud(
                "Solicitud encontrada",
                "Descripción de la solicitud"
        );

        when(solicitudRepository.findById(1L))
                .thenReturn(Optional.of(solicitud));

        // Act
        Optional<Solicitud> resultado =
                solicitudService.obtenerPorId(1L);

        // Assert
        assertEquals(true, resultado.isPresent());
        assertEquals(
                "Solicitud encontrada",
                resultado.get().getTitulo()
        );
     }
     @Test
        void debeRetornarVacioCuandoSolicitudNoExiste() {

        // Arrange
        when(solicitudRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Solicitud> resultado =
                solicitudService.obtenerPorId(99L);

        // Assert
        assertEquals(true, resultado.isEmpty());
     }

     @Test
        void debeCrearSolicitud() {

        // Arrange
        Solicitud solicitud = new Solicitud(
                "Nueva solicitud",
                "Descripción de la nueva solicitud"
        );

        when(solicitudRepository.save(solicitud))
                .thenReturn(solicitud);

        // Act
        Solicitud resultado =
                solicitudService.crear(solicitud);

        // Assert
        assertEquals(
                "Nueva solicitud",
                resultado.getTitulo()
        );

        assertEquals(
                "Descripción de la nueva solicitud",
                resultado.getDescripcion()
        );

        verify(solicitudRepository).save(solicitud);
    }

    @Test
        void debeActualizarSolicitud() {

        // Arrange
        Solicitud solicitudExistente = new Solicitud(
                "Título anterior",
                "Descripción anterior"
        );

        Solicitud solicitudActualizada = new Solicitud(
                "Título actualizado",
                "Descripción actualizada"
        );

        solicitudActualizada.setEstado(
                com.solicitudpro.solicitudpro_api.model.EstadoSolicitud.EN_PROCESO
        );

        when(solicitudRepository.findById(1L))
                .thenReturn(Optional.of(solicitudExistente));

        when(solicitudRepository.save(solicitudExistente))
                .thenReturn(solicitudExistente);

        // Act
        Solicitud resultado =
                solicitudService.actualizar(
                        1L,
                        solicitudActualizada
                );

        // Assert
        assertEquals(
                "Título actualizado",
                resultado.getTitulo()
        );

        assertEquals(
                "Descripción actualizada",
                resultado.getDescripcion()
        );

        assertEquals(
                com.solicitudpro.solicitudpro_api.model.EstadoSolicitud.EN_PROCESO,
                resultado.getEstado()
        );

        verify(solicitudRepository).save(solicitudExistente);
    }

    @Test
        void debeLanzarExcepcionAlActualizarSolicitudInexistente() {

        // Arrange
        Solicitud solicitudActualizada = new Solicitud(
                "Título actualizado",
                "Descripción actualizada"
        );

        solicitudActualizada.setEstado(
                com.solicitudpro.solicitudpro_api.model.EstadoSolicitud.EN_PROCESO
        );

        when(solicitudRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        SolicitudNotFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        SolicitudNotFoundException.class,
                        () -> solicitudService.actualizar(
                                99L,
                                solicitudActualizada
                        )
                );

        assertEquals(
                "Solicitud no encontrada con ID: 99",
                exception.getMessage()
        );
   }
   @Test
        void debeEliminarSolicitud() {

        // Arrange
        when(solicitudRepository.existsById(1L))
                .thenReturn(true);

        // Act
        solicitudService.eliminar(1L);

        // Assert
        verify(solicitudRepository).deleteById(1L);
   }
   @Test
        void debeLanzarExcepcionAlEliminarSolicitudInexistente() {

        // Arrange
        when(solicitudRepository.existsById(99L))
                .thenReturn(false);

        // Act + Assert
        SolicitudNotFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        SolicitudNotFoundException.class,
                        () -> solicitudService.eliminar(99L)
                );

        assertEquals(
                "Solicitud no encontrada con ID: 99",
                exception.getMessage()
        );

        verify(solicitudRepository, org.mockito.Mockito.never())
                .deleteById(99L);
    }
}