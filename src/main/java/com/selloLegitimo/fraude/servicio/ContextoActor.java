package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.excepcion.ExcepcionAutorizacion;
import com.selloLegitimo.fraude.modelo.RolActorControl;

/**
 * Identidad y rol del actor que ejecuta la peticion. Se construye desde los
 * headers HTTP {@code X-Actor-Id} y {@code X-Actor-Rol} y se propaga al
 * servicio para auditar cada operacion.
 *
 * <p>La autorizacion es por rol: solo los cuatro roles de Actores de Control
 * pueden operar SR-M6. Cualquier otro rol dispara {@link ExcepcionAutorizacion}
 * mapeada a HTTP 403.</p>
 */
public record ContextoActor(String actorId, RolActorControl rol) {

	public ContextoActor {
		if (actorId == null || actorId.isBlank()) {
			throw new ExcepcionAutorizacion("Header X-Actor-Id ausente o vacio");
		}
		if (rol == null) {
			throw new ExcepcionAutorizacion("Header X-Actor-Rol ausente o invalido");
		}
	}

	/**
	 * Construye el contexto a partir de los headers crudos, validando que el
	 * rol forme parte del catalogo de Actores de Control autorizados.
	 *
	 * @throws ExcepcionAutorizacion si falta header, el rol no existe o no
	 *         pertenece al subconjunto autorizado para SR-M6.
	 */
	public static ContextoActor desdeHeaders(String actorId, String rolCrudo) {
		if (rolCrudo == null || !RolActorControl.esAutorizado(rolCrudo)) {
			throw new ExcepcionAutorizacion(
				"Rol no autorizado para operar casos de investigacion: " + rolCrudo);
		}
		return new ContextoActor(actorId, RolActorControl.valueOf(rolCrudo));
	}
}
