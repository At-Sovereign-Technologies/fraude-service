package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.modelo.EvidenciaReferencia;
import com.selloLegitimo.fraude.repositorio.RepositorioEvidencia;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioEvidencia {

    private static final Logger log = LoggerFactory.getLogger(ServicioEvidencia.class);

    private final RepositorioEvidencia repositorio;

    public ServicioEvidencia(RepositorioEvidencia repositorio) {
        this.repositorio = repositorio;
    }

    public String generarSiguienteReferenceId() {
        int year = Year.now().getValue();
        Optional<EvidenciaReferencia> ultima = repositorio.findTopByOrderByCreatedAtDesc();
        int nextSeq = 1;

        if (ultima.isPresent()) {
            String lastRef = ultima.get().getReferenceId();
            try {
                String[] parts = lastRef.split("-");
                if (parts.length == 3) {
                    int lastYear = Integer.parseInt(parts[1]);
                    int lastSeq = Integer.parseInt(parts[2]);
                    nextSeq = (lastYear == year) ? lastSeq + 1 : 1;
                }
            } catch (NumberFormatException e) {
                log.warn("No se pudo parsear referenceId '{}', arrancando desde 1", lastRef);
            }
        }

        String refId = String.format("REF-%04d-%06d", year, nextSeq);
        log.info("Siguiente referenceId generado: {}", refId);
        return refId;
    }
}
