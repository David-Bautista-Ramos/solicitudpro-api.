
package com.solicitudpro.solicitudpro_api.service;

import com.solicitudpro.solicitudpro_api.exception.SolicitudNotFoundException;
import com.solicitudpro.solicitudpro_api.model.Solicitud;
import com.solicitudpro.solicitudpro_api.model.Usuario;
import com.solicitudpro.solicitudpro_api.repository.SolicitudRepository;
import com.solicitudpro.solicitudpro_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudService(
            SolicitudRepository solicitudRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Solicitud> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    public Optional<Solicitud> obtenerPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    @Transactional
    public Solicitud crear(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizar(
            Long id,
            Solicitud solicitudActualizada) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(
                        () -> new SolicitudNotFoundException(id)
                );

        solicitud.setTitulo(solicitudActualizada.getTitulo());
        solicitud.setDescripcion(
                solicitudActualizada.getDescripcion()
        );
        solicitud.setEstado(solicitudActualizada.getEstado());

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new SolicitudNotFoundException(id);
        }

        solicitudRepository.deleteById(id);
    }

    // OPERACIONES CON CONTROL DE PROPIETARIO

    public List<Solicitud> obtenerPorAcceso(
            String correo,
            boolean esAdmin) {

        if (esAdmin) {
            return solicitudRepository.findAll();
        }

        Usuario usuario = buscarUsuarioAutenticado(correo);

        return solicitudRepository.findAllByPropietarioId(
                usuario.getId()
        );
    }

    public Solicitud obtenerPorIdConAcceso(
            Long id,
            String correo,
            boolean esAdmin) {

        if (esAdmin) {
            return solicitudRepository.findById(id)
                    .orElseThrow(
                            () -> new SolicitudNotFoundException(id)
                    );
        }

        Usuario usuario = buscarUsuarioAutenticado(correo);

        return solicitudRepository
                .findByIdAndPropietarioId(id, usuario.getId())
                .orElseThrow(
                        () -> new SolicitudNotFoundException(id)
                );
    }

    @Transactional
    public Solicitud crearParaUsuario(
            Solicitud solicitud,
            String correo) {

        Usuario usuario = buscarUsuarioAutenticado(correo);
        solicitud.setPropietario(usuario);

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarConAcceso(
            Long id,
            Solicitud solicitudActualizada,
            String correo,
            boolean esAdmin) {

        Solicitud solicitud = obtenerPorIdConAcceso(
                id, correo, esAdmin
        );

        solicitud.setTitulo(solicitudActualizada.getTitulo());
        solicitud.setDescripcion(
                solicitudActualizada.getDescripcion()
        );
        solicitud.setEstado(solicitudActualizada.getEstado());

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void eliminarConAcceso(
            Long id,
            String correo,
            boolean esAdmin) {

        Solicitud solicitud = obtenerPorIdConAcceso(
                id, correo, esAdmin
        );

        solicitudRepository.delete(solicitud);
    }

    private Usuario buscarUsuarioAutenticado(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario autenticado no encontrado"
                ));
    }
}