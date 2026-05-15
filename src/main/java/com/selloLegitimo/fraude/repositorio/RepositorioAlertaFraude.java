package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioAlertaFraude extends JpaRepository<AlertaFraude, Long> {

	Optional<AlertaFraude> findByAlertUuid(UUID alertUuid);

	Optional<AlertaFraude> findByOriginModuleAndOriginEventId(String originModule, String originEventId);

	Page<AlertaFraude> findByStatus(EstadoAlerta status, Pageable pageable);

	Page<AlertaFraude> findBySeverityLevel(String severityLevel, Pageable pageable);

	Page<AlertaFraude> findByTypologyId(String typologyId, Pageable pageable);

	Page<AlertaFraude> findByOriginModule(String originModule, Pageable pageable);

	@Query("SELECT COUNT(a) FROM AlertaFraude a")
	long countTotal();

	@Query("SELECT a.severityLevel, COUNT(a) FROM AlertaFraude a GROUP BY a.severityLevel")
	List<Object[]> countBySeverity();

	@Query("SELECT a.status, COUNT(a) FROM AlertaFraude a GROUP BY a.status")
	List<Object[]> countByStatus();

	@Query("SELECT a.typologyId, COUNT(a) FROM AlertaFraude a GROUP BY a.typologyId")
	List<Object[]> countByTypology();
}
