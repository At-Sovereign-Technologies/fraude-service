package com.selloLegitimo.fraude.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rbac")
public class PermisoConfig {

    private Map<String, Map<String, List<String>>> permissions = Collections.emptyMap();

    public Map<String, Map<String, List<String>>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Map<String, List<String>>> permissions) {
        this.permissions = permissions;
    }

    public boolean isPermitted(String role, String resource, String operation) {
        Map<String, List<String>> rolePerms = permissions.get(role);
        if (rolePerms == null) {
            return false;
        }
        List<String> ops = rolePerms.get(resource);
        if (ops == null) {
            return false;
        }
        return ops.contains(operation);
    }

    public List<String> getAllowedOperations(String role, String resource) {
        Map<String, List<String>> rolePerms = permissions.get(role);
        if (rolePerms == null) {
            return Collections.emptyList();
        }
        return rolePerms.getOrDefault(resource, Collections.emptyList());
    }
}
