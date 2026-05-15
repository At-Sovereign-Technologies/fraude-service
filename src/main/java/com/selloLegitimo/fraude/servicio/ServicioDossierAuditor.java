package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.dto.AuditEntryResponse;
import com.selloLegitimo.fraude.dto.CierreCasoResponse;
import com.selloLegitimo.fraude.dto.DossierAuditorResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionAccesoDenegado;
import com.selloLegitimo.fraude.modelo.CierreCaso;
import com.selloLegitimo.fraude.modelo.RegistroAuditoriaCaso;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioDossierAuditor {

    private static final Logger log = LoggerFactory.getLogger(ServicioDossierAuditor.class);

    private final ServicioCierreCaso servicioCierreCaso;
    private final ServicioAuditoriaCadena servicioAuditoriaCadena;
    private final ClienteConfiguracionEleccion clienteConfiguracion;

    public ServicioDossierAuditor(
            ServicioCierreCaso servicioCierreCaso,
            ServicioAuditoriaCadena servicioAuditoriaCadena,
            ClienteConfiguracionEleccion clienteConfiguracion) {
        this.servicioCierreCaso = servicioCierreCaso;
        this.servicioAuditoriaCadena = servicioAuditoriaCadena;
        this.clienteConfiguracion = clienteConfiguracion;
    }

    public DossierAuditorResponse obtenerDossier(String role) {
        if (role == null || (!"ACCREDITED_AUDITOR".equals(role) && !"AUDITOR".equals(role) && !"ADMINISTRADOR".equals(role) && !"SUPERADMIN".equals(role))) {
            throw new ExcepcionAccesoDenegado(
                "Solo un auditor o administrador puede acceder al dossier");
        }

        String electionStatus = clienteConfiguracion.consultarEstadoEleccion();
        if ("BORRADOR".equals(electionStatus)) {
            throw new ExcepcionAccesoDenegado(
                "El dossier no esta disponible mientras la eleccion esta en estado BORRADOR. Estado actual: "
                    + electionStatus);
        }

        List<CierreCaso> closures = servicioCierreCaso.listarTodos();
        boolean cadenaValida = servicioAuditoriaCadena.verificarCadena();

        DossierAuditorResponse dossier = new DossierAuditorResponse();
        dossier.setElectionStatus(electionStatus);
        dossier.setTotalClosedCases(closures.size());
        long confirmed = closures.stream()
            .filter(c -> c.getFinalResult() == com.selloLegitimo.fraude.modelo.ResultadoFinal.CONFIRMED_FRAUD)
            .count();
        dossier.setConfirmedFraudCount((int) confirmed);
        dossier.setDismissedCount((int) (closures.size() - confirmed));
        dossier.setClosures(closures.stream()
            .map(this::convertirCierre)
            .toList());
        dossier.setAuditChain(servicioAuditoriaCadena.obtenerTodas().stream()
            .map(this::convertirAuditEntry)
            .toList());
        dossier.setChainIntegrityVerified(cadenaValida);

        log.info("[DOSSIER] generado con {} cierres, cadena_valida={}",
            closures.size(), cadenaValida);

        return dossier;
    }

    private CierreCasoResponse convertirCierre(CierreCaso cierre) {
        CierreCasoResponse response = new CierreCasoResponse();
        response.setAlertUuid(cierre.getAlertUuid().toString());
        response.setFinalResult(cierre.getFinalResult().name());
        response.setJustification(cierre.getJustification());
        response.setInstitutionalActions(
            cierre.getInstitutionalActions() != null
                ? cierre.getInstitutionalActions().replaceAll("[\\[\\]\"]", "").split(",")
                : new String[0]);
        response.setResponsibleEntity(cierre.getResponsibleEntity());
        response.setActorId(cierre.getActorId());
        response.setActorRole(cierre.getActorRole());
        response.setClosureTimestamp(cierre.getClosureTimestamp());
        response.setSignature(cierre.getSignature());
        return response;
    }

    private AuditEntryResponse convertirAuditEntry(RegistroAuditoriaCaso entry) {
        AuditEntryResponse response = new AuditEntryResponse();
        response.setId(entry.getId());
        response.setAlertUuid(entry.getAlertUuid() != null ? entry.getAlertUuid().toString() : null);
        response.setActorId(entry.getActorId());
        response.setRole(entry.getRole());
        response.setFromStatus(entry.getFromStatus());
        response.setToStatus(entry.getToStatus());
        response.setTransitionTimestamp(entry.getTransitionTimestamp());
        response.setRecordHash(entry.getRecordHash());
        response.setPreviousHash(entry.getPreviousHash());
        response.setClosureUuid(entry.getClosureUuid() != null ? entry.getClosureUuid().toString() : null);
        return response;
    }
}
