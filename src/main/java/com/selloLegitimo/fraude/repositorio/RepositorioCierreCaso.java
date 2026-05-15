package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.CierreCaso;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioCierreCaso extends JpaRepository<CierreCaso, Long> {

    Optional<CierreCaso> findByAlertUuid(UUID alertUuid);

    boolean existsByAlertUuid(UUID alertUuid);
}
