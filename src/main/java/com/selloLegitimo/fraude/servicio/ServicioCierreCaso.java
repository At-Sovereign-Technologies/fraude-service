package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.dto.CerrarCasoRequest;
import com.selloLegitimo.fraude.dto.CierreCasoResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionCasoYaCerrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.CierreCaso;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.modelo.ResultadoFinal;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import com.selloLegitimo.fraude.repositorio.RepositorioAuditoria;
import com.selloLegitimo.fraude.repositorio.RepositorioCierreCaso;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioCierreCaso {

    private static final Logger log = LoggerFactory.getLogger(ServicioCierreCaso.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RepositorioAlertaFraude repositorioAlerta;
    private final RepositorioCierreCaso repositorioCierre;
    private final RepositorioAuditoria repositorioAuditoria;
    private final ServicioAuditoriaCadena servicioAuditoriaCadena;

    public ServicioCierreCaso(
            RepositorioAlertaFraude repositorioAlerta,
            RepositorioCierreCaso repositorioCierre,
            RepositorioAuditoria repositorioAuditoria,
            ServicioAuditoriaCadena servicioAuditoriaCadena) {
        this.repositorioAlerta = repositorioAlerta;
        this.repositorioCierre = repositorioCierre;
        this.repositorioAuditoria = repositorioAuditoria;
        this.servicioAuditoriaCadena = servicioAuditoriaCadena;
    }

    @Transactional
    public CierreCasoResponse cerrarCaso(
            UUID alertUuid,
            CerrarCasoRequest request,
            String actorId,
            String actorRole) {

        AlertaFraude alerta = repositorioAlerta.findByAlertUuid(alertUuid)
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                "No existe alerta con uuid " + alertUuid));

        if (alerta.getStatus() == EstadoAlerta.CERRADO) {
            throw new ExcepcionCasoYaCerrado(
                "La alerta " + alertUuid + " ya se encuentra en estado CERRADO");
        }

        if (alerta.getStatus() != EstadoAlerta.CONFIRMADO
            && alerta.getStatus() != EstadoAlerta.DESCARTADO) {
            throw new ExcepcionFraude(
                "Solo se puede cerrar una alerta en estado CONFIRMADO o DESCARTADO. Estado actual: "
                    + alerta.getStatus());
        }

        boolean yaCerrado = repositorioCierre.existsByAlertUuid(alertUuid);
        if (yaCerrado) {
            throw new ExcepcionCasoYaCerrado(
                "Ya existe un registro de cierre para la alerta " + alertUuid);
        }

        CierreCaso cierre = new CierreCaso();
        cierre.setAlertUuid(alertUuid);
        cierre.setFinalResult(request.getFinalResult());
        cierre.setJustification(request.getJustification().trim());
        cierre.setInstitutionalActions(serializarLista(request.getInstitutionalActions()));
        cierre.setResponsibleEntity(request.getResponsibleEntity().trim());
        cierre.setActorId(actorId);
        cierre.setActorRole(actorRole);
        cierre.setClosureTimestamp(LocalDateTime.now());

        String signature = firmarRegistro(cierre, actorId);
        cierre.setSignature(signature);

        CierreCaso guardado = repositorioCierre.save(cierre);

        EstadoAlerta estadoAnterior = alerta.getStatus();
        alerta.setStatus(EstadoAlerta.CERRADO);
        alerta.setClosedAt(LocalDateTime.now());
        alerta.setClosedBy(actorId);
        alerta.setLastActorId(actorId);
        alerta.setLastTransitionAt(LocalDateTime.now());
        repositorioAlerta.save(alerta);

        servicioAuditoriaCadena.escribirEntrada(
            alertUuid,
            actorId,
            actorRole,
            estadoAnterior.name(),
            EstadoAlerta.CERRADO.name(),
            "{\"finalResult\":\"" + request.getFinalResult() + "\"}",
            alertUuid);

        log.info("[SR-M6] CASO CERRADO alerta={} resultado={} entidad={} actor={} firma={}",
            alertUuid, request.getFinalResult(), request.getResponsibleEntity(),
            actorId, signature.substring(0, 16));

        return convertirARespuesta(guardado);
    }

    public CierreCasoResponse obtenerCierre(UUID alertUuid) {
        CierreCaso cierre = repositorioCierre.findByAlertUuid(alertUuid)
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                "No existe cierre para la alerta " + alertUuid));
        return convertirARespuesta(cierre);
    }

    public List<CierreCaso> listarTodos() {
        return repositorioCierre.findAll();
    }

    public long countByResult(ResultadoFinal resultado) {
        return repositorioCierre.findAll().stream()
            .filter(c -> c.getFinalResult() == resultado)
            .count();
    }

    private String firmarRegistro(CierreCaso cierre, String actorToken) {
        String canonical = cierre.getAlertUuid().toString() + "|"
            + cierre.getFinalResult().name() + "|"
            + cierre.getJustification() + "|"
            + cierre.getInstitutionalActions() + "|"
            + cierre.getResponsibleEntity() + "|"
            + cierre.getActorId() + "|"
            + cierre.getClosureTimestamp().toString();

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(
                actorToken.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] hmac = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar el registro de cierre", e);
        }
    }

    private String serializarLista(List<String> items) {
        try {
            return MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando institutionalActions", e);
        }
    }

    private CierreCasoResponse convertirARespuesta(CierreCaso cierre) {
        CierreCasoResponse response = new CierreCasoResponse();
        response.setAlertUuid(cierre.getAlertUuid().toString());
        response.setFinalResult(cierre.getFinalResult().name());
        response.setJustification(cierre.getJustification());
        response.setInstitutionalActions(deserializarLista(cierre.getInstitutionalActions()));
        response.setResponsibleEntity(cierre.getResponsibleEntity());
        response.setActorId(cierre.getActorId());
        response.setActorRole(cierre.getActorRole());
        response.setClosureTimestamp(cierre.getClosureTimestamp());
        response.setSignature(cierre.getSignature());
        return response;
    }

    private String[] deserializarLista(String json) {
        try {
            List<String> list = MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, String.class));
            return list.toArray(new String[0]);
        } catch (JsonProcessingException e) {
            log.warn("Error deserializando institutionalActions", e);
            return new String[0];
        }
    }
}
