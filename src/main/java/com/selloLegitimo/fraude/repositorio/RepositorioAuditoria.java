package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.RegistroAuditoriaCaso;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioAuditoria extends JpaRepository<RegistroAuditoriaCaso, Long> {

    List<RegistroAuditoriaCaso> findByAlertUuidOrderByTransitionTimestampDesc(UUID alertUuid);
}
