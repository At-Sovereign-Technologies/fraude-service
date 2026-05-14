package com.selloLegitimo.fraude.modelo;

import java.util.Set;

/**
 * Roles de Actores de Control autorizados a operar el modulo SR-M6.
 * Cualquier acceso desde un rol no listado debe retornar 403.
 */
public enum RolActorControl {
	ADMIN_RNEC,
	DELEGADO_CNE,
	DELEGADO_MESA_JUSTICIA,
	DELEGADO_FISCALIA;

	private static final Set<String> NOMBRES_AUTORIZADOS = Set.of(
		ADMIN_RNEC.name(),
		DELEGADO_CNE.name(),
		DELEGADO_MESA_JUSTICIA.name(),
		DELEGADO_FISCALIA.name()
	);

	/**
	 * Devuelve true si {@code rol} es uno de los roles autorizados para SR-M6.
	 */
	public static boolean esAutorizado(String rol) {
		return rol != null && NOMBRES_AUTORIZADOS.contains(rol);
	}
}
