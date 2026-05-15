package com.selloLegitimo.fraude.config;

import com.selloLegitimo.fraude.seguridad.InterceptorPermisos;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PermisoConfig permisoConfig;

    public WebConfig(PermisoConfig permisoConfig) {
        this.permisoConfig = permisoConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorPermisos(permisoConfig))
            .addPathPatterns("/api/v1/fraude/**")
            .excludePathPatterns(
                "/api/v1/fraude/health",
                "/api/v1/fraude/metrics",
                "/api/v1/auth/**");
    }
}
