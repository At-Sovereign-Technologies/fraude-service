package com.selloLegitimo.fraude.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Esquema uniforme de error para SR-M6.
 *
 * <pre>
 * {
 *   "error": "TRANSICION_INVALIDA",
 *   "mensaje": "...",
 *   "radicado": "uuid-del-caso",
 *   "timestamp": "2025-10-15T14:32:00Z"
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private String error;
	private String mensaje;
	private UUID radicado;
	private LocalDateTime timestamp;

	public ErrorResponse(String error, String mensaje, UUID radicado) {
		this.error = error;
		this.mensaje = mensaje;
		this.radicado = radicado;
		this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
	}

	public ErrorResponse(String error, String mensaje) {
		this(error, mensaje, null);
	}

	public String getError() { return error; }
	public void setError(String error) { this.error = error; }

	public String getMensaje() { return mensaje; }
	public void setMensaje(String mensaje) { this.mensaje = mensaje; }

	public UUID getRadicado() { return radicado; }
	public void setRadicado(UUID radicado) { this.radicado = radicado; }

	public LocalDateTime getTimestamp() { return timestamp; }
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
