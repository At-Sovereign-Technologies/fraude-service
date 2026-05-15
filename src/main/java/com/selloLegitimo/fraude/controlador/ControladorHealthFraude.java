package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.MetricasAlertasResponse;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude")
public class ControladorHealthFraude {

	private final RepositorioAlertaFraude repositorio;

	public ControladorHealthFraude(RepositorioAlertaFraude repositorio) {
		this.repositorio = repositorio;
	}

	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> health() {
		return ResponseEntity.ok(Map.of(
			"status", "UP",
			"module", "SR-M8",
			"version", "1.0.0"
		));
	}

	@GetMapping("/metrics")
	public ResponseEntity<MetricasAlertasResponse> metrics() {
		MetricasAlertasResponse response = new MetricasAlertasResponse();
		response.setTotalAlerts(repositorio.countTotal());
		response.setBySeverity(toMap(repositorio.countBySeverity()));
		response.setByStatus(toMap(repositorio.countByStatus()));
		response.setByTypology(toMap(repositorio.countByTypology()));
		return ResponseEntity.ok(response);
	}

	private Map<String, Long> toMap(java.util.List<Object[]> rows) {
		return rows.stream()
			.collect(Collectors.toMap(
				r -> r[0] instanceof EstadoAlerta ? ((EstadoAlerta) r[0]).name() : (String) r[0],
				r -> (Long) r[1],
				(a, b) -> a + b,
				LinkedHashMap::new
			));
	}
}
