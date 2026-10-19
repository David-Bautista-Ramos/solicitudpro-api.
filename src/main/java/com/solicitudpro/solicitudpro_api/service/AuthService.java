
package com.solicitudpro.solicitudpro_api.service;

import com.solicitudpro.solicitudpro_api.dto.AuthResponse;
import com.solicitudpro.solicitudpro_api.dto.LoginRequest;
import com.solicitudpro.solicitudpro_api.dto.RegistroRequest;
import com.solicitudpro.solicitudpro_api.model.RolUsuario;
import com.solicitudpro.solicitudpro_api.model.Usuario;
import com.solicitudpro.solicitudpro_api.repository.UsuarioRepository;
import com.solicitudpro.solicitudpro_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String correo = request.correo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setPassword(
                passwordEncoder.encode(request.password())
        );
        usuario.setRol(RolUsuario.USUARIO);

        Usuario guardado = usuarioRepository.save(usuario);

        return crearRespuesta(guardado);
    }

    public AuthResponse iniciarSesion(LoginRequest request) {
        String correo = request.correo().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
            );
        }

        return crearRespuesta(usuario);
    }

    private AuthResponse crearRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario.getCorreo());

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }
}