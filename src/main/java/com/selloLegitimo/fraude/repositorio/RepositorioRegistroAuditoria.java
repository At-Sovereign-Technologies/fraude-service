package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.RegistroAuditoria;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioRegistroAuditoria extends JpaRepository<RegistroAuditoria, UUID> {

	/**
	 * Historial completo de auditoria de un caso ordenado cronologicamente.
	 */
	List<RegistroAuditoria> findByRadicadoCasoOrderByTimestampAsc(UUID radicadoCaso);
}
