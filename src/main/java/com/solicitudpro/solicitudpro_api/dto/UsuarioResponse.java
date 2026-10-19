
package com.solicitudpro.solicitudpro_api.dto;

import com.solicitudpro.solicitudpro_api.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol,
        LocalDateTime fechaCreacion
) {
    public UsuarioResponse(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getFechaCreacion()
        );
    }
}