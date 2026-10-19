package com.solicitudpro.solicitudpro_api.controller;

import com.solicitudpro.solicitudpro_api.exception.SolicitudNotFoundException;
import com.solicitudpro.solicitudpro_api.mapper.SolicitudMapper;
import com.solicitudpro.solicitudpro_api.model.EstadoSolicitud;
import com.solicitudpro.solicitudpro_api.model.Solicitud;
import com.solicitudpro.solicitudpro_api.repository.UsuarioRepository;
import com.solicitudpro.solicitudpro_api.security.JwtService;
import com.solicitudpro.solicitudpro_api.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.solicitudpro.solicitudpro_api.security.JwtAuthenticationFilter;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(SolicitudController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SolicitudMapper.class)

class SolicitudControllerTest {

        private static final String CORREO_USUARIO = "usuario@solicitudpro.com";

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
    private SolicitudService solicitudService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UsuarioRepository usuarioRepository;

        @MockitoBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        private UsernamePasswordAuthenticationToken usuarioAutenticado() {
                return new UsernamePasswordAuthenticationToken(
                                CORREO_USUARIO, null, List.of()
                );
        }

    @Test
    void debeObtenerTodasLasSolicitudes() throws Exception {

        // Arrange
        Solicitud solicitud = new Solicitud(
                "Solicitud de prueba",
                "Descripción de prueba"
        );

        when(solicitudService.obtenerPorAcceso(
                "usuario@solicitudpro.com", false))
                .thenReturn(List.of(solicitud));

        // Act + Assert
        mockMvc.perform(
                get("/api/solicitudes").principal(usuarioAutenticado())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].titulo")
                .value("Solicitud de prueba"))
        .andExpect(jsonPath("$[0].descripcion")
                .value("Descripción de prueba"));
    }

    @Test
        void debeObtenerSolicitudPorId() throws Exception {

        // Arrange
        Solicitud solicitud = new Solicitud(
                "Solicitud encontrada",
                "Descripción de la solicitud"
        );

        when(solicitudService.obtenerPorIdConAcceso(
                1L, "usuario@solicitudpro.com", false))
                .thenReturn(solicitud);

        // Act + Assert
        mockMvc.perform(
                get("/api/solicitudes/1").principal(usuarioAutenticado())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.titulo")
                .value("Solicitud encontrada"))
        .andExpect(jsonPath("$.descripcion")
                .value("Descripción de la solicitud"));
    }

    @Test
        void debeRetornar404CuandoSolicitudNoExiste() throws Exception {

        // Arrange
        when(solicitudService.obtenerPorIdConAcceso(
                999L, "usuario@solicitudpro.com", false))
                .thenThrow(new SolicitudNotFoundException(999L));

        // Act + Assert
        mockMvc.perform(
                get("/api/solicitudes/999").principal(usuarioAutenticado())
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message")
                .value("Solicitud no encontrada con ID: 999"));
   }

   @Test
        void debeCrearSolicitud() throws Exception {

        when(solicitudService.crearParaUsuario(
                any(Solicitud.class), eq("usuario@solicitudpro.com")))
                .thenAnswer(invocation -> {
                        Solicitud solicitud = invocation.getArgument(0);
                        solicitud.setId(1L);
                        return solicitud;
                });

        mockMvc.perform(post("/api/solicitudes")
                .principal(usuarioAutenticado())
                .contentType("application/json")
                .content("""
                        {
                                "titulo": "Solicitud nueva",
                                "descripcion": "Descripción de prueba"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Solicitud nueva"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
        void debeRechazarSolicitudSinDatosObligatorios() throws Exception {

        mockMvc.perform(post("/api/solicitudes")
                .principal(usuarioAutenticado())
                .contentType("application/json")
                .content("""
                        {
                                "titulo": "",
                                "descripcion": ""
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
   }

   @Test
        void debeActualizarSolicitud() throws Exception {

        Solicitud actualizada = new Solicitud(
                "Solicitud actualizada",
                "Nueva descripción"
        );
        actualizada.setId(1L);
        actualizada.setEstado(EstadoSolicitud.EN_PROCESO);

        when(solicitudService.actualizarConAcceso(
                eq(1L), any(Solicitud.class),
                eq("usuario@solicitudpro.com"), eq(false)))
                .thenReturn(actualizada);

        mockMvc.perform(put("/api/solicitudes/1")
                .principal(usuarioAutenticado())
                .contentType("application/json")
                .content("""
                        {
                                "titulo": "Solicitud actualizada",
                                "descripcion": "Nueva descripción",
                                "estado": "EN_PROCESO"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Solicitud actualizada"))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

        @Test
        void debeRetornar404AlActualizarSolicitudInexistente() throws Exception {

        when(solicitudService.actualizarConAcceso(
                eq(999L), any(Solicitud.class),
                eq("usuario@solicitudpro.com"), eq(false)))
                .thenThrow(new SolicitudNotFoundException(999L));

        mockMvc.perform(put("/api/solicitudes/999")
                .principal(usuarioAutenticado())
                .contentType("application/json")
                .content("""
                        {
                                "titulo": "Solicitud",
                                "descripcion": "Descripción",
                                "estado": "PENDIENTE"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        void debeEliminarSolicitud() throws Exception {

        mockMvc.perform(delete("/api/solicitudes/1")
                .principal(usuarioAutenticado()))
                .andExpect(status().isNoContent());

        verify(solicitudService).eliminarConAcceso(
                1L, "usuario@solicitudpro.com", false);
        }

        @Test
        void debeRetornar404AlEliminarSolicitudInexistente() throws Exception {

        doThrow(new SolicitudNotFoundException(999L))
                .when(solicitudService).eliminarConAcceso(
                        999L, "usuario@solicitudpro.com", false);

        mockMvc.perform(delete("/api/solicitudes/999")
                .principal(usuarioAutenticado()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
        }


}
