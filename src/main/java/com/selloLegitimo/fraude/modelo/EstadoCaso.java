package com.selloLegitimo.fraude.modelo;

import java.util.Map;
import java.util.Set;

/**
 * Estados del ciclo de vida de un {@link CasoInvestigacion}.
 *
 * <p>Secuencia: DETECTADO -&gt; EN_EVALUACION -&gt; EN_INVESTIGACION -&gt; ESCALADO
 * -&gt; (CONFIRMADO | DESCARTADO) -&gt; CERRADO.</p>
 *
 * <p>Las transiciones son estrictamente lineales y avanzan en una sola direccion.
 * CERRADO es terminal e irreversible.</p>
 */
public enum EstadoCaso {
	DETECTADO,
	EN_EVALUACION,
	EN_INVESTIGACION,
	ESCALADO,
	CONFIRMADO,
	DESCARTADO,
	CERRADO;

	private static final Map<EstadoCaso, Set<EstadoCaso>> TRANSICIONES = Map.of(
		DETECTADO,        Set.of(EN_EVALUACION),
		EN_EVALUACION,    Set.of(EN_INVESTIGACION),
		EN_INVESTIGACION, Set.of(ESCALADO),
		ESCALADO,         Set.of(CONFIRMADO, DESCARTADO),
		CONFIRMADO,       Set.of(CERRADO),
		DESCARTADO,       Set.of(CERRADO),
		CERRADO,          Set.of()
	);

	/**
	 * Devuelve los estados destino legales desde este estado.
	 */
	public Set<EstadoCaso> transicionesPermitidas() {
		return TRANSICIONES.get(this);
	}

	/**
	 * Indica si la transicion {@code this -> destino} es legal en la maquina de estados.
	 */
	public boolean puedeTransicionarA(EstadoCaso destino) {
		return destino != null && transicionesPermitidas().contains(destino);
	}

	/**
	 * Verdadero si el estado es terminal e irreversible.
	 */
	public boolean esTerminal() {
		return this == CERRADO;
	}
}
