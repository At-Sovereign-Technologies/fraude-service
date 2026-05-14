package com.selloLegitimo.fraude.excepcion;

/**
 * Lanzada cuando el actor que invoca la API no presenta credenciales o su rol
 * no esta autorizado para operar SR-M6. Mapea a HTTP 403.
 */
public class ExcepcionAutorizacion extends ExcepcionFraude {

	public ExcepcionAutorizacion(String mensaje) {
		super(mensaje);
	}
}
