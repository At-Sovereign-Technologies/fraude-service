package com.selloLegitimo.fraude.excepcion;

import java.util.UUID;

/**
 * Lanzada cuando se intenta una transicion no permitida por la maquina de
 * estados del caso (e.g. saltar etapas, retroceder).
 */
public class ExcepcionTransicionInvalida extends ExcepcionFraude {

	private final UUID radicado;

	public ExcepcionTransicionInvalida(UUID radicado, String mensaje) {
		super(mensaje);
		this.radicado = radicado;
	}

	public UUID getRadicado() { return radicado; }
}
