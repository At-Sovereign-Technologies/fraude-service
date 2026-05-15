package com.selloLegitimo.fraude.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmacionEntregaRequest {

    @NotBlank(message = "targetRole es obligatorio")
    private String targetRole;

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
}
