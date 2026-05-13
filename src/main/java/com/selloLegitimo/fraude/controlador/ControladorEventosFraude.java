package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.AlertaFraudeResponse;
import com.selloLegitimo.fraude.dto.ReportarEventoRequest;
import com.selloLegitimo.fraude.servicio.ProcesadorEventosFraude;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude")
public class ControladorEventosFraude {

	private static final Logger logger = LoggerFactory.getLogger(ControladorEventosFraude.class);

	private final ProcesadorEventosFraude procesador;

	public ControladorEventosFraude(ProcesadorEventosFraude procesador) {
		this.procesador = procesador;
	}

	@PostMapping("/eventos")
	public ResponseEntity<AlertaFraudeResponse> reportarEvento(
			@Valid @RequestBody ReportarEventoRequest request) {
		logger.info("Evento recibido source={} eventType={} originEventId={}",
			request.getSource(), request.getEventType(), request.getOriginEventId());
		AlertaFraudeResponse response = procesador.procesar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
