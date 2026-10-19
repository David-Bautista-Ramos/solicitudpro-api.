
package com.solicitudpro.solicitudpro_api.dto;

public record AuthResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nombre,
        String correo,
        String rol
) {
}