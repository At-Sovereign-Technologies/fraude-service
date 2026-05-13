package com.selloLegitimo.fraude.excepcion;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

	private static final Logger logger = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

	@ExceptionHandler(ExcepcionHashNoCoincide.class)
	public ResponseEntity<Map<String, String>> handleHashNoCoincide(ExcepcionHashNoCoincide e) {
		logger.warn("Hash mismatch: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", e.getMessage()));
	}

	@ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
	public ResponseEntity<Map<String, String>> handleNoEncontrado(ExcepcionRecursoNoEncontrado e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(Map.of("error", e.getMessage()));
	}

	@ExceptionHandler(ExcepcionFraude.class)
	public ResponseEntity<Map<String, String>> handleFraude(ExcepcionFraude e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
		String mensaje = e.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getField() + ": " + err.getDefaultMessage())
			.reduce((a, b) -> a + "; " + b)
			.orElse("Error de validacion");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", mensaje));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
		logger.error("Error no esperado", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Map.of("error", "Error interno del servidor"));
	}
}
