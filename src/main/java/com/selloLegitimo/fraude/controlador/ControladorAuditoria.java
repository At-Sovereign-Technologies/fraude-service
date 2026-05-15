package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.DossierAuditorResponse;
import com.selloLegitimo.fraude.seguridad.RequierePermiso;
import com.selloLegitimo.fraude.servicio.ServicioAuditoriaCadena;
import com.selloLegitimo.fraude.servicio.ServicioDossierAuditor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/auditoria")
public class ControladorAuditoria {

    private final ServicioDossierAuditor servicioDossier;
    private final ServicioAuditoriaCadena servicioAuditoriaCadena;

    public ControladorAuditoria(
            ServicioDossierAuditor servicioDossier,
            ServicioAuditoriaCadena servicioAuditoriaCadena) {
        this.servicioDossier = servicioDossier;
        this.servicioAuditoriaCadena = servicioAuditoriaCadena;
    }

    @GetMapping("/dossier")
    @RequierePermiso(recurso = "AUDITORIA", operacion = "READ_DOSSIER")
    public ResponseEntity<DossierAuditorResponse> obtenerDossier(HttpServletRequest request) {
        String role = (String) request.getAttribute("sr-m7-role");
        DossierAuditorResponse dossier = servicioDossier.obtenerDossier(role);
        return ResponseEntity.ok(dossier);
    }

    @GetMapping("/cadena/verificar")
    @RequierePermiso(recurso = "AUDITORIA", operacion = "VERIFY_CHAIN")
    public ResponseEntity<Map<String, Object>> verificarCadena() {
        boolean valida = servicioAuditoriaCadena.verificarCadena();
        return ResponseEntity.ok(Map.of(
            "chainIntegrityVerified", valida,
            "status", valida ? "INTEGRA" : "COMPROMETIDA"));
    }
}
