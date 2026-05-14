package com.selloLegitimo.fraude.excepcion;

import java.util.UUID;

/**
 * Lanzada cuando se intenta operar (modificar, transicionar) sobre un caso
 * que ya esta en estado CERRADO. El cierre es irreversible.
 */
public class ExcepcionCasoCerrado extends ExcepcionFraude {

	private final UUID radicado;

	public ExcepcionCasoCerrado(UUID radicado, String mensaje) {
		super(mensaje);
		this.radicado = radicado;
	}

	public UUID getRadicado() { return radicado; }
}
