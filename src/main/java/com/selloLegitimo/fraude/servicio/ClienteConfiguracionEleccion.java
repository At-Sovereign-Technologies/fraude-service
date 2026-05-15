package com.selloLegitimo.fraude.servicio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClienteConfiguracionEleccion {

    private static final Logger log = LoggerFactory.getLogger(ClienteConfiguracionEleccion.class);
    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private volatile Boolean cachedActivePhase;
    private volatile LocalDateTime cacheExpiresAt;

    public ClienteConfiguracionEleccion(
            @Value("${eleccion.service.url:http://configuracion-eleccion:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public boolean existeFaseElectoralActiva() {
        if (cachedActivePhase != null && cacheExpiresAt != null
            && LocalDateTime.now().isBefore(cacheExpiresAt)) {
            return cachedActivePhase;
        }

        try {
            String json = restTemplate.getForObject(baseUrl + "/api/elecciones", String.class);
            List<Map<String, Object>> elecciones = objectMapper.readValue(json,
                new TypeReference<List<Map<String, Object>>>() {});

            boolean active = elecciones.stream()
                .anyMatch(e -> "EN_CURSO".equals(e.get("estado")));

            cachedActivePhase = active;
            cacheExpiresAt = LocalDateTime.now().plus(CACHE_TTL);
            log.info("Fase electoral activa={} ({} elecciones consultadas)", active, elecciones.size());
            return active;
        } catch (Exception e) {
            log.warn("No se pudo consultar ConfiguracionEleccion, asumiendo inactivo: {}", e.getMessage());
            cachedActivePhase = false;
            cacheExpiresAt = LocalDateTime.now().plus(CACHE_TTL);
            return false;
        }
    }

    public String consultarEstadoEleccion() {
        try {
            String json = restTemplate.getForObject(baseUrl + "/api/elecciones", String.class);
            List<Map<String, Object>> elecciones = objectMapper.readValue(json,
                new TypeReference<List<Map<String, Object>>>() {});

            if (elecciones.isEmpty()) {
                log.info("No hay elecciones registradas");
                return "NO_ELECTIONS";
            }

            String latestStatus = elecciones.stream()
                .map(e -> (String) e.get("estado"))
                .filter(s -> s != null)
                .reduce((first, second) -> second)
                .orElse("UNKNOWN");

            log.info("Estado de eleccion mas reciente={}", latestStatus);
            return latestStatus;
        } catch (Exception e) {
            log.warn("No se pudo consultar estado de eleccion: {}", e.getMessage());
            return "UNKNOWN";
        }
    }
}
