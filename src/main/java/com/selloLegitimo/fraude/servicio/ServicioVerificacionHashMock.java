package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.excepcion.ExcepcionEvidenciaInvalida;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServicioVerificacionHashMock {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(ServicioVerificacionHashMock.class);

    public boolean verificar(String referenceId, String hashSignature, LocalDateTime originalTimestamp) {
        String canonical = referenceId + "|" + originalTimestamp.format(FMT);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(canonical.getBytes());
            String computed = HexFormat.of().formatHex(digest);
            boolean valid = computed.equals(hashSignature);
            log.info("[SR-MOCK HASH] referenceId={} valid={}", referenceId, valid);
            if (!valid) {
                throw new ExcepcionEvidenciaInvalida(
                    "Hash invalido para referenceId=" + referenceId
                        + ". Esperado=" + computed + " recibido=" + hashSignature);
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}
