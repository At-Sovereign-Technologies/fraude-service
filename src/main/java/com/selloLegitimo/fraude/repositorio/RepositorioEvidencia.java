package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.EvidenciaReferencia;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioEvidencia extends JpaRepository<EvidenciaReferencia, UUID> {

    List<EvidenciaReferencia> findByAlertUuid(UUID alertUuid);

    Optional<EvidenciaReferencia> findTopByOrderByCreatedAtDesc();
}
