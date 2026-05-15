package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.excepcion.ExcepcionCadenaRota;
import com.selloLegitimo.fraude.modelo.RegistroAuditoriaCaso;
import com.selloLegitimo.fraude.repositorio.RepositorioAuditoria;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioAuditoriaCadena {

    private static final Logger log = LoggerFactory.getLogger(ServicioAuditoriaCadena.class);
    private static final String GENESIS_HASH = HexFormat.of().formatHex(
        sha256Raw("GENESIS_AUDIT_CHAIN_SR_M6"));

    private final RepositorioAuditoria repositorioAuditoria;

    public ServicioAuditoriaCadena(RepositorioAuditoria repositorioAuditoria) {
        this.repositorioAuditoria = repositorioAuditoria;
    }

    @Transactional
    public RegistroAuditoriaCaso escribirEntrada(
            UUID alertUuid,
            String actorId,
            String role,
            String fromStatus,
            String toStatus,
            String metadata,
            UUID closureUuid) {

        String previousHash = obtenerUltimoHash().orElse(GENESIS_HASH);

        RegistroAuditoriaCaso entrada = new RegistroAuditoriaCaso();
        entrada.setAlertUuid(alertUuid);
        entrada.setActorId(actorId);
        entrada.setRole(role);
        entrada.setFromStatus(fromStatus);
        entrada.setToStatus(toStatus);
        entrada.setTransitionTimestamp(LocalDateTime.now());
        entrada.setMetadata(metadata);
        entrada.setClosureUuid(closureUuid);
        entrada.setPreviousHash(previousHash);

        String recordHash = computeHash(entrada);
        entrada.setRecordHash(recordHash);

        RegistroAuditoriaCaso guardada = repositorioAuditoria.save(entrada);
        log.info("[AUDIT CHAIN] entry={} alert={} from={} to={} actor={} hash={} prev={}",
            guardada.getId(), alertUuid, fromStatus, toStatus, actorId,
            recordHash.substring(0, 12), previousHash.substring(0, 12));

        return guardada;
    }

    public boolean verificarCadena() {
        List<RegistroAuditoriaCaso> entries = repositorioAuditoria.findAll(
            Sort.by(Sort.Direction.ASC, "id"));

        if (entries.isEmpty()) {
            return true;
        }

        String expectedPrevious = GENESIS_HASH;

        for (RegistroAuditoriaCaso entry : entries) {
            if (!expectedPrevious.equals(entry.getPreviousHash())) {
                log.error("[CHAIN BREAK] entry={} expected_prev={} actual_prev={}",
                    entry.getId(), expectedPrevious.substring(0, 12),
                    entry.getPreviousHash() != null ? entry.getPreviousHash().substring(0, 12) : "null");
                return false;
            }
            String computed = computeHash(entry);
            if (!computed.equals(entry.getRecordHash())) {
                log.error("[CHAIN BREAK] entry={} hash_mismatch computed={} stored={}",
                    entry.getId(), computed.substring(0, 12),
                    entry.getRecordHash().substring(0, 12));
                return false;
            }
            expectedPrevious = entry.getRecordHash();
        }

        return true;
    }

    public void verificarCadenaOrThrow() {
        if (!verificarCadena()) {
            throw new ExcepcionCadenaRota(
                "La cadena de auditoria ha sido comprometida: los hashes no coinciden");
        }
    }

    public List<RegistroAuditoriaCaso> obtenerTodas() {
        return repositorioAuditoria.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    private Optional<String> obtenerUltimoHash() {
        return repositorioAuditoria.findAll(
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")))
            .stream()
            .findFirst()
            .map(RegistroAuditoriaCaso::getRecordHash);
    }

    private String computeHash(RegistroAuditoriaCaso entry) {
        String canonical = (entry.getId() != null ? entry.getId().toString() : "null") + "|"
            + (entry.getAlertUuid() != null ? entry.getAlertUuid().toString() : "null") + "|"
            + (entry.getActorId() != null ? entry.getActorId() : "null") + "|"
            + (entry.getRole() != null ? entry.getRole() : "null") + "|"
            + (entry.getFromStatus() != null ? entry.getFromStatus() : "null") + "|"
            + (entry.getToStatus() != null ? entry.getToStatus() : "null") + "|"
            + (entry.getTransitionTimestamp() != null ? entry.getTransitionTimestamp().toString() : "null") + "|"
            + (entry.getMetadata() != null ? entry.getMetadata() : "null") + "|"
            + (entry.getPreviousHash() != null ? entry.getPreviousHash() : "null");
        return HexFormat.of().formatHex(sha256Raw(canonical));
    }

    private static byte[] sha256Raw(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}
