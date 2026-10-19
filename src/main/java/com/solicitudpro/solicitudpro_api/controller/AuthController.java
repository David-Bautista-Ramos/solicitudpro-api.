
package com.solicitudpro.solicitudpro_api.controller;

import com.solicitudpro.solicitudpro_api.dto.AuthResponse;
import com.solicitudpro.solicitudpro_api.dto.LoginRequest;
import com.solicitudpro.solicitudpro_api.dto.RegistroRequest;
import com.solicitudpro.solicitudpro_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(
            @Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    public AuthResponse iniciarSesion(
            @Valid @RequestBody LoginRequest request) {
        return authService.iniciarSesion(request);
    }
}