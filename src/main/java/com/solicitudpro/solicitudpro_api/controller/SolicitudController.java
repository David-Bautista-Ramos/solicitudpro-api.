
package com.solicitudpro.solicitudpro_api.controller;

import com.solicitudpro.solicitudpro_api.dto.SolicitudRequest;
import com.solicitudpro.solicitudpro_api.dto.SolicitudResponse;
import com.solicitudpro.solicitudpro_api.dto.SolicitudUpdateRequest;
import com.solicitudpro.solicitudpro_api.exception.ErrorResponse;
import com.solicitudpro.solicitudpro_api.mapper.SolicitudMapper;
import com.solicitudpro.solicitudpro_api.model.Solicitud;
import com.solicitudpro.solicitudpro_api.service.SolicitudService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/solicitudes")
@Tag(
    name = "Solicitud",
    description = "Operaciones relacionadas con las solicitudes"
)
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SolicitudMapper solicitudMapper;

    public SolicitudController(
            SolicitudService solicitudService,
            SolicitudMapper solicitudMapper) {
        this.solicitudService = solicitudService;
        this.solicitudMapper = solicitudMapper;
    }

    
private boolean esAdministrador(Authentication authentication) {
    if (authentication == null) {
        return false;
    }

    return authentication.getAuthorities()
            .stream()
            .filter(Objects::nonNull)
            .map(autoridad -> autoridad.getAuthority())
            .anyMatch("ROLE_ADMIN"::equals);
}


    @Operation(
        summary = "Obtener solicitudes",
        description = "El usuario consulta las propias y el administrador consulta todas"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas correctamente"),
        @ApiResponse(
            responseCode = "401",
            description = "Usuario no autenticado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> obtenerTodas(
            Authentication authentication) {

        List<SolicitudResponse> respuesta =
                solicitudService.obtenerPorAcceso(
                        authentication.getName(),
                        esAdministrador(authentication)
                )
                .stream()
                .map(solicitudMapper::toResponse)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
        summary = "Obtener una solicitud por ID",
        description = "Consulta una solicitud propia o cualquier solicitud si es administrador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada o sin acceso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerPorId(
            @Parameter(description = "ID de la solicitud", example = "1")
            @PathVariable Long id,
            Authentication authentication) {

        Solicitud solicitud =
                solicitudService.obtenerPorIdConAcceso(
                        id,
                        authentication.getName(),
                        esAdministrador(authentication)
                );

        return ResponseEntity.ok(
                solicitudMapper.toResponse(solicitud)
        );
    }

    @Operation(
        summary = "Crear una solicitud",
        description = "Crea una solicitud y asigna automáticamente el usuario autenticado como propietario"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Solicitud creada correctamente"),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(
            @Valid @RequestBody SolicitudRequest request,
            Authentication authentication) {

        Solicitud solicitud = solicitudMapper.toEntity(request);

        Solicitud nuevaSolicitud =
                solicitudService.crearParaUsuario(
                        solicitud,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(solicitudMapper.toResponse(nuevaSolicitud));
    }

    @Operation(
        summary = "Actualizar una solicitud",
        description = "Actualiza una solicitud propia o cualquier solicitud si es administrador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitud actualizada correctamente"),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada o sin acceso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudResponse> actualizar(
            @Parameter(description = "ID de la solicitud", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SolicitudUpdateRequest request,
            Authentication authentication) {

        Solicitud solicitud =
                solicitudMapper.toEntity(request);

        Solicitud actualizada =
                solicitudService.actualizarConAcceso(
                        id,
                        solicitud,
                        authentication.getName(),
                        esAdministrador(authentication)
                );

        return ResponseEntity.ok(
                solicitudMapper.toResponse(actualizada)
        );
    }

    @Operation(
        summary = "Eliminar una solicitud",
        description = "Elimina una solicitud propia o cualquier solicitud si es administrador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitud eliminada correctamente"),
        @ApiResponse(
            responseCode = "404",
            description = "Solicitud no encontrada o sin acceso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la solicitud", example = "1")
            @PathVariable Long id,
            Authentication authentication) {

        solicitudService.eliminarConAcceso(
                id,
                authentication.getName(),
                esAdministrador(authentication)
        );

        return ResponseEntity.noContent().build();
    }
}
