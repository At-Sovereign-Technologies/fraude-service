package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.EntregaNotificacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioEntregaNotificacion extends JpaRepository<EntregaNotificacion, Long> {

    List<EntregaNotificacion> findByAlertUuid(UUID alertUuid);

    @Query("SELECT e FROM EntregaNotificacion e "
        + "WHERE e.deliveredAt IS NULL "
        + "AND e.escalatedAt IS NULL "
        + "AND e.generatedAt < :threshold")
    List<EntregaNotificacion> findPendingBefore(LocalDateTime threshold);
}
