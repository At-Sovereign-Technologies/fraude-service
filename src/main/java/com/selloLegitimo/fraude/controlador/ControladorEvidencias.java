package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.CrearEvidenciaRequest;
import com.selloLegitimo.fraude.dto.EvidenciaResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionEstadoInmutable;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.modelo.EvidenciaReferencia;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import com.selloLegitimo.fraude.repositorio.RepositorioEvidencia;
import com.selloLegitimo.fraude.servicio.ServicioEvidencia;
import com.selloLegitimo.fraude.servicio.ServicioVerificacionHashMock;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/evidencias")
public class ControladorEvidencias {

    private static final Logger log = LoggerFactory.getLogger(ControladorEvidencias.class);

    private final RepositorioEvidencia repositorioEvidencia;
    private final RepositorioAlertaFraude repositorioAlertas;
    private final ServicioVerificacionHashMock verificacionHash;
    private final ServicioEvidencia servicioEvidencia;

    public ControladorEvidencias(RepositorioEvidencia repositorioEvidencia,
        RepositorioAlertaFraude repositorioAlertas,
        ServicioVerificacionHashMock verificacionHash,
        ServicioEvidencia servicioEvidencia) {
        this.repositorioEvidencia = repositorioEvidencia;
        this.repositorioAlertas = repositorioAlertas;
        this.verificacionHash = verificacionHash;
        this.servicioEvidencia = servicioEvidencia;
    }

    @GetMapping("/next-reference-id")
    public ResponseEntity<Map<String, String>> obtenerSiguienteReferenceId() {
        String refId = servicioEvidencia.generarSiguienteReferenceId();
        return ResponseEntity.ok(Map.of("referenceId", refId));
    }

    @PostMapping
    public ResponseEntity<EvidenciaResponse> asociar(@Valid @RequestBody CrearEvidenciaRequest request) {
        AlertaFraude alerta = repositorioAlertas.findByAlertUuid(request.getAlertUuid())
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                "No existe alerta con uuid " + request.getAlertUuid()));

        if (alerta.getStatus() == EstadoAlerta.CERRADO) {
            throw new ExcepcionEstadoInmutable(
                "No se puede asociar evidencia a una alerta en estado CERRADO");
        }

        verificacionHash.verificar(request.getReferenceId(), request.getHashSignature(), request.getOriginalTimestamp());

        EvidenciaReferencia evidencia = new EvidenciaReferencia();
        evidencia.setAlertUuid(request.getAlertUuid());
        evidencia.setReferenceId(request.getReferenceId());
        evidencia.setHashSignature(request.getHashSignature());
        evidencia.setOriginalTimestamp(request.getOriginalTimestamp());
        evidencia.setVerified(true);
        evidencia.setVerifiedAt(LocalDateTime.now());

        EvidenciaReferencia guardada = repositorioEvidencia.save(evidencia);
        log.info("Evidencia asociada: alerta={} referenceId={} evidenciaId={}",
            request.getAlertUuid(), request.getReferenceId(), guardada.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(EvidenciaResponse.fromEntity(guardada));
    }

    @GetMapping("/alerta/{alertUuid}")
    public List<EvidenciaResponse> listarPorAlerta(@PathVariable UUID alertUuid) {
        if (!repositorioAlertas.findByAlertUuid(alertUuid).isPresent()) {
            throw new ExcepcionRecursoNoEncontrado("No existe alerta con uuid " + alertUuid);
        }
        return repositorioEvidencia.findByAlertUuid(alertUuid).stream()
            .map(EvidenciaResponse::fromEntity)
            .toList();
    }
}
