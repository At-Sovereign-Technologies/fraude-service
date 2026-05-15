package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.CerrarCasoRequest;
import com.selloLegitimo.fraude.dto.CierreCasoResponse;
import com.selloLegitimo.fraude.seguridad.RequierePermiso;
import com.selloLegitimo.fraude.servicio.ServicioCierreCaso;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/casos")
public class ControladorCierreCaso {

    private final ServicioCierreCaso servicioCierreCaso;

    public ControladorCierreCaso(ServicioCierreCaso servicioCierreCaso) {
        this.servicioCierreCaso = servicioCierreCaso;
    }

    @PostMapping("/{alertUuid}/cierre")
    @RequierePermiso(recurso = "CIERRE", operacion = "CREATE")
    public ResponseEntity<CierreCasoResponse> cerrarCaso(
            @PathVariable UUID alertUuid,
            @Valid @RequestBody CerrarCasoRequest request,
            HttpServletRequest httpRequest) {
        String actorId = resolveActorId(httpRequest);
        String actorRole = resolveActorRole(httpRequest);
        CierreCasoResponse response = servicioCierreCaso.cerrarCaso(
            alertUuid, request, actorId, actorRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{alertUuid}/cierre")
    @RequierePermiso(recurso = "CIERRE", operacion = "READ")
    public ResponseEntity<CierreCasoResponse> obtenerCierre(@PathVariable UUID alertUuid) {
        CierreCasoResponse response = servicioCierreCaso.obtenerCierre(alertUuid);
        return ResponseEntity.ok(response);
    }

    private String resolveActorId(HttpServletRequest request) {
        String id = request.getHeader("X-User-Id");
        return id != null && !id.isBlank() ? id : "unknown";
    }

    private String resolveActorRole(HttpServletRequest request) {
        String role = (String) request.getAttribute("sr-m7-role");
        return role != null ? role : "UNKNOWN";
    }
}
