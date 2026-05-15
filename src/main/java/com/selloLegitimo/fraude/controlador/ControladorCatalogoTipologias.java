package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.CrearTipologiaRequest;
import com.selloLegitimo.fraude.dto.TipologiaResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionAccesoDenegado;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.modelo.Tipologia;
import com.selloLegitimo.fraude.repositorio.RepositorioTipologia;
import com.selloLegitimo.fraude.servicio.ClienteConfiguracionEleccion;
import com.selloLegitimo.fraude.servicio.ServicioNotificacionCNEMock;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/tipologias")
public class ControladorCatalogoTipologias {

    private static final Logger log = LoggerFactory.getLogger(ControladorCatalogoTipologias.class);

    private final RepositorioTipologia repositorio;
    private final ClienteConfiguracionEleccion clienteEleccion;
    private final ServicioNotificacionCNEMock notificacionCNE;

    public ControladorCatalogoTipologias(RepositorioTipologia repositorio,
        ClienteConfiguracionEleccion clienteEleccion,
        ServicioNotificacionCNEMock notificacionCNE) {
        this.repositorio = repositorio;
        this.clienteEleccion = clienteEleccion;
        this.notificacionCNE = notificacionCNE;
    }

    @GetMapping
    public List<TipologiaResponse> listar() {
        return repositorio.findAll().stream()
            .map(TipologiaResponse::fromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public TipologiaResponse obtener(@PathVariable String id) {
        Tipologia tipologia = repositorio.findById(id)
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tipologia no encontrada: " + id));
        return TipologiaResponse.fromEntity(tipologia);
    }

    @PostMapping
    public ResponseEntity<TipologiaResponse> crear(
            @Valid @RequestBody CrearTipologiaRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verificarAdminRole(role);
        verificarFaseActiva(request.getJustification(), request.getId(), "CREAR");

        Tipologia tipologia = new Tipologia();
        tipologia.setId(request.getId());
        tipologia.setName(request.getName());
        tipologia.setDescription(request.getDescription());
        tipologia.setDefaultSeverity(request.getDefaultSeverity());
        tipologia.setRequiresReview(request.getRequiresReview() != null ? request.getRequiresReview() : true);
        tipologia.setCreatedAt(LocalDateTime.now());
        tipologia.setUpdatedAt(LocalDateTime.now());

        Tipologia guardada = repositorio.save(tipologia);
        log.info("Tipologia creada: id={} name={}", guardada.getId(), guardada.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(TipologiaResponse.fromEntity(guardada));
    }

    @PutMapping("/{id}")
    public TipologiaResponse actualizar(
            @PathVariable String id,
            @Valid @RequestBody CrearTipologiaRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verificarAdminRole(role);
        verificarFaseActiva(request.getJustification(), id, "ACTUALIZAR");

        Tipologia existente = repositorio.findById(id)
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tipologia no encontrada: " + id));

        existente.setName(request.getName());
        existente.setDescription(request.getDescription());
        existente.setDefaultSeverity(request.getDefaultSeverity());
        existente.setRequiresReview(request.getRequiresReview() != null ? request.getRequiresReview() : true);
        existente.setUpdatedAt(LocalDateTime.now());

        Tipologia guardada = repositorio.save(existente);
        log.info("Tipologia actualizada: id={}", guardada.getId());
        return TipologiaResponse.fromEntity(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        verificarAdminRole(role);
        verificarFaseActiva("Eliminacion sin justificacion detallada", id, "ELIMINAR");

        if (!repositorio.existsById(id)) {
            throw new ExcepcionRecursoNoEncontrado("Tipologia no encontrada: " + id);
        }
        repositorio.deleteById(id);
        log.info("Tipologia eliminada: id={}", id);
        return ResponseEntity.noContent().build();
    }

    private void verificarAdminRole(String role) {
        String upperRole = role == null ? "" : role.toUpperCase();
        if (!"SUPERADMIN".equals(upperRole) && !"ADMINISTRADOR".equals(upperRole) && !"ADMIN_RNEC".equals(upperRole)) {
            throw new ExcepcionAccesoDenegado(
                "Solo un administrador puede modificar el catalogo de tipologias. Rol actual: " + role);
        }
    }

    private void verificarFaseActiva(String justification, String tipologiaId, String accion) {
        if (clienteEleccion.existeFaseElectoralActiva()) {
            if (justification == null || justification.isBlank()) {
                throw new ExcepcionFraude(
                    "Fase electoral activa: se requiere 'justification' para " + accion + " tipologia " + tipologiaId);
            }
            String notifId = notificacionCNE.notificarModificacionTipologia(tipologiaId, accion, justification);
            log.info("CNE notificado: tipologia={} accion={} notifId={}", tipologiaId, accion, notifId);
        }
    }
}
