package com.selloLegitimo.fraude.excepcion;

import com.selloLegitimo.fraude.dto.ErrorResponse;
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

	@ExceptionHandler(ExcepcionCasoCerrado.class)
	public ResponseEntity<ErrorResponse> handleCasoCerrado(ExcepcionCasoCerrado e) {
		logger.warn("Operacion sobre caso CERRADO {}: {}", e.getRadicado(), e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("CASO_CERRADO", e.getMessage(), e.getRadicado()));
	}

	@ExceptionHandler(ExcepcionTransicionInvalida.class)
	public ResponseEntity<ErrorResponse> handleTransicionInvalida(ExcepcionTransicionInvalida e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("TRANSICION_INVALIDA", e.getMessage(), e.getRadicado()));
	}

	@ExceptionHandler(ExcepcionAutorizacion.class)
	public ResponseEntity<ErrorResponse> handleAutorizacion(ExcepcionAutorizacion e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ErrorResponse("ACCESO_DENEGADO", e.getMessage()));
	}

	@ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
	public ResponseEntity<ErrorResponse> handleNoEncontrado(ExcepcionRecursoNoEncontrado e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("RECURSO_NO_ENCONTRADO", e.getMessage()));
	}

	@ExceptionHandler(ExcepcionFraude.class)
	public ResponseEntity<ErrorResponse> handleFraude(ExcepcionFraude e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("VALIDACION_FALLIDA", e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
		String mensaje = e.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getField() + ": " + err.getDefaultMessage())
			.reduce((a, b) -> a + "; " + b)
			.orElse("Error de validacion");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("VALIDACION_FALLIDA", mensaje));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("VALIDACION_FALLIDA", e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
		logger.error("Error no esperado", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("ERROR_INTERNO", "Error interno del servidor"));
	}
}
