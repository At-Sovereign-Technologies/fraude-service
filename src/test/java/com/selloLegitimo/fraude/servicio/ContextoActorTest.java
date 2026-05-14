package com.selloLegitimo.fraude.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.selloLegitimo.fraude.excepcion.ExcepcionAutorizacion;
import com.selloLegitimo.fraude.modelo.RolActorControl;
import org.junit.jupiter.api.Test;

/**
 * Autorizacion por rol: cualquier rol fuera del catalogo de Actores de Control
 * autorizados para SR-M6 debe ser rechazado.
 */
class ContextoActorTest {

	@Test
	void rolAutorizadoConstruyeContexto() {
		ContextoActor ctx = ContextoActor.desdeHeaders("user-1", "DELEGADO_CNE");
		assertThat(ctx.actorId()).isEqualTo("user-1");
		assertThat(ctx.rol()).isEqualTo(RolActorControl.DELEGADO_CNE);
	}

	@Test
	void rolNoAutorizadoEsRechazado() {
		assertThatThrownBy(() -> ContextoActor.desdeHeaders("user-1", "VOTANTE"))
			.isInstanceOf(ExcepcionAutorizacion.class)
			.hasMessageContaining("VOTANTE");
	}

	@Test
	void rolNullEsRechazado() {
		assertThatThrownBy(() -> ContextoActor.desdeHeaders("user-1", null))
			.isInstanceOf(ExcepcionAutorizacion.class);
	}

	@Test
	void actorIdAusenteEsRechazado() {
		assertThatThrownBy(() -> ContextoActor.desdeHeaders(null, "ADMIN_RNEC"))
			.isInstanceOf(ExcepcionAutorizacion.class);
		assertThatThrownBy(() -> ContextoActor.desdeHeaders("  ", "ADMIN_RNEC"))
			.isInstanceOf(ExcepcionAutorizacion.class);
	}
}
