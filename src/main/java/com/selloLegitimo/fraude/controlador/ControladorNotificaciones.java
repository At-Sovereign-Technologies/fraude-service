package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.ConfirmacionEntregaRequest;
import com.selloLegitimo.fraude.servicio.ServicioNotificacionCritica;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/notificaciones")
public class ControladorNotificaciones {

    private final ServicioNotificacionCritica servicioNotificacion;

    public ControladorNotificaciones(ServicioNotificacionCritica servicioNotificacion) {
        this.servicioNotificacion = servicioNotificacion;
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarEntrega(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmacionEntregaRequest request) {
        servicioNotificacion.confirmarEntrega(id, request.getTargetRole());
        return ResponseEntity.ok(Map.of(
            "status", "DELIVERY_CONFIRMED",
            "deliveryId", id,
            "targetRole", request.getTargetRole()));
    }
}
