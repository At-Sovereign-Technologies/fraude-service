package com.selloLegitimo.fraude.modelo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Verifica la maquina de estados: todas las transiciones validas e invalidas.
 * Invariante de seguridad: la secuencia es monotonica y CERRADO es terminal.
 */
class EstadoCasoTest {

	static Stream<Arguments> transicionesValidas() {
		return Stream.of(
			Arguments.of(EstadoCaso.DETECTADO, EstadoCaso.EN_EVALUACION),
			Arguments.of(EstadoCaso.EN_EVALUACION, EstadoCaso.EN_INVESTIGACION),
			Arguments.of(EstadoCaso.EN_INVESTIGACION, EstadoCaso.ESCALADO),
			Arguments.of(EstadoCaso.ESCALADO, EstadoCaso.CONFIRMADO),
			Arguments.of(EstadoCaso.ESCALADO, EstadoCaso.DESCARTADO),
			Arguments.of(EstadoCaso.CONFIRMADO, EstadoCaso.CERRADO),
			Arguments.of(EstadoCaso.DESCARTADO, EstadoCaso.CERRADO)
		);
	}

	@ParameterizedTest(name = "{0} -> {1} es valida")
	@MethodSource("transicionesValidas")
	void transicionesPermitidasSonLegales(EstadoCaso origen, EstadoCaso destino) {
		assertThat(origen.puedeTransicionarA(destino)).isTrue();
	}

	@Test
	@DisplayName("Toda transicion no listada en la tabla es ilegal")
	void todasLasTransicionesNoListadasSonIlegales() {
		Set<EstadoCaso> todos = Set.of(EstadoCaso.values());
		for (EstadoCaso origen : todos) {
			Set<EstadoCaso> permitidas = origen.transicionesPermitidas();
			for (EstadoCaso destino : todos) {
				if (!permitidas.contains(destino)) {
					assertThat(origen.puedeTransicionarA(destino))
						.as("%s -> %s deberia ser ilegal", origen, destino)
						.isFalse();
				}
			}
		}
	}

	@Test
	void retrocederNuncaEsLegal() {
		assertThat(EstadoCaso.EN_INVESTIGACION.puedeTransicionarA(EstadoCaso.EN_EVALUACION)).isFalse();
		assertThat(EstadoCaso.EN_EVALUACION.puedeTransicionarA(EstadoCaso.DETECTADO)).isFalse();
		assertThat(EstadoCaso.ESCALADO.puedeTransicionarA(EstadoCaso.EN_INVESTIGACION)).isFalse();
		assertThat(EstadoCaso.CONFIRMADO.puedeTransicionarA(EstadoCaso.ESCALADO)).isFalse();
	}

	@Test
	void saltarEtapasNuncaEsLegal() {
		assertThat(EstadoCaso.DETECTADO.puedeTransicionarA(EstadoCaso.EN_INVESTIGACION)).isFalse();
		assertThat(EstadoCaso.DETECTADO.puedeTransicionarA(EstadoCaso.ESCALADO)).isFalse();
		assertThat(EstadoCaso.DETECTADO.puedeTransicionarA(EstadoCaso.CERRADO)).isFalse();
		assertThat(EstadoCaso.EN_EVALUACION.puedeTransicionarA(EstadoCaso.ESCALADO)).isFalse();
	}

	@Test
	void cerradoEsTerminalSinTransiciones() {
		assertThat(EstadoCaso.CERRADO.esTerminal()).isTrue();
		assertThat(EstadoCaso.CERRADO.transicionesPermitidas()).isEmpty();
		for (EstadoCaso destino : EstadoCaso.values()) {
			assertThat(EstadoCaso.CERRADO.puedeTransicionarA(destino))
				.as("CERRADO -> %s debe ser ilegal", destino)
				.isFalse();
		}
	}

	@Test
	void escaladoSeBifurcaEnConfirmadoYDescartado() {
		assertThat(EstadoCaso.ESCALADO.transicionesPermitidas())
			.containsExactlyInAnyOrder(EstadoCaso.CONFIRMADO, EstadoCaso.DESCARTADO);
	}

	@Test
	void puedeTransicionarConNullEsFalse() {
		assertThat(EstadoCaso.DETECTADO.puedeTransicionarA(null)).isFalse();
	}
}
