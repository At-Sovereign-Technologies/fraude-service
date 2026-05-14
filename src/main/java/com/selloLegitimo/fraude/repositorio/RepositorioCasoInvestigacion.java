package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.CasoInvestigacion;
import com.selloLegitimo.fraude.modelo.EntidadCompetente;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.NivelPrioridad;
import com.selloLegitimo.fraude.modelo.TipologiaFraude;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioCasoInvestigacion extends JpaRepository<CasoInvestigacion, UUID> {

	/**
	 * Filtro paginado y dinamico sobre los atributos de busqueda exigidos por la
	 * especificacion (estado, prioridad, tipologia, entidad, responsable).
	 * Cada parametro nulo se ignora.
	 */
	@Query("""
		SELECT c FROM CasoInvestigacion c
		WHERE (:estado        IS NULL OR c.estado = :estado)
		  AND (:prioridad     IS NULL OR c.nivelPrioridad = :prioridad)
		  AND (:tipologia     IS NULL OR c.tipologiaFraude = :tipologia)
		  AND (:entidad       IS NULL OR c.entidadCompetente = :entidad)
		  AND (:responsable   IS NULL OR c.responsableInstitucional = :responsable)
		""")
	Page<CasoInvestigacion> buscarConFiltros(
		@Param("estado") EstadoCaso estado,
		@Param("prioridad") NivelPrioridad prioridad,
		@Param("tipologia") TipologiaFraude tipologia,
		@Param("entidad") EntidadCompetente entidad,
		@Param("responsable") String responsable,
		Pageable pageable);

	/**
	 * Recupera todos los radicados cuyo conjunto exacto de alertas origen coincide
	 * con el de entrada. Soporta la regla de idempotencia: dos llamadas de creacion
	 * con las mismas alertas devuelven el mismo caso.
	 *
	 * <p>La condicion {@code COUNT(DISTINCT) = :tamano} en HAVING asegura que el
	 * caso candidato contenga todas las alertas de entrada (y solo esas) tras
	 * filtrar por miembros y agrupar por radicado con conteo total.</p>
	 */
	@Query("""
		SELECT c.radicado FROM CasoInvestigacion c
		JOIN c.alertasOrigen a
		WHERE a IN :alertas
		GROUP BY c.radicado, SIZE(c.alertasOrigen)
		HAVING COUNT(DISTINCT a) = :tamano AND SIZE(c.alertasOrigen) = :tamano
		""")
	List<UUID> buscarRadicadosPorConjuntoExacto(
		@Param("alertas") List<UUID> alertas,
		@Param("tamano") long tamano);

	Optional<CasoInvestigacion> findByRadicado(UUID radicado);
}
