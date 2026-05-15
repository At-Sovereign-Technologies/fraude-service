package com.selloLegitimo.fraude.seguridad;

import com.selloLegitimo.fraude.config.PermisoConfig;
import com.selloLegitimo.fraude.excepcion.ExcepcionAccesoDenegado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class InterceptorPermisos implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(InterceptorPermisos.class);

    private final PermisoConfig permisoConfig;

    public InterceptorPermisos(PermisoConfig permisoConfig) {
        this.permisoConfig = permisoConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequierePermiso annotation = handlerMethod.getMethodAnnotation(RequierePermiso.class);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequierePermiso.class);
        }
        if (annotation == null) {
            return true;
        }

        String role = resolveRole(request);
        String recurso = annotation.recurso();
        String operacion = annotation.operacion();

        boolean granted = permisoConfig.isPermitted(role, recurso, operacion);

        log.info("[SR-M7 AUDIT] recurso={} operacion={} rol={} usuario={} resultado={} timestamp={}",
            recurso, operacion, role,
            request.getHeader("X-User-Id") != null ? request.getHeader("X-User-Id") : "unknown",
            granted ? "GRANTED" : "DENIED",
            LocalDateTime.now());

        if (!granted) {
            throw new ExcepcionAccesoDenegado(
                "Acceso denegado: no tiene permiso para " + operacion + " en " + recurso);
        }

        request.setAttribute("sr-m7-role", role);
        request.setAttribute("sr-m7-user-id", request.getHeader("X-User-Id"));

        return true;
    }

    private String resolveRole(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (role == null || role.isBlank()) {
            throw new ExcepcionAccesoDenegado("Cabecera X-User-Role no presente");
        }
        return role.toUpperCase();
    }
}
