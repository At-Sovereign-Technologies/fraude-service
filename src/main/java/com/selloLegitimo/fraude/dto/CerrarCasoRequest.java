package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.ResultadoFinal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CerrarCasoRequest {

    @NotNull(message = "finalResult es obligatorio (CONFIRMED_FRAUD o DISMISSED)")
    private ResultadoFinal finalResult;

    @NotBlank(message = "justification es obligatoria y no puede estar vacia")
    private String justification;

    @NotEmpty(message = "institutionalActions debe contener al menos una accion")
    private List<@NotBlank String> institutionalActions;

    @NotBlank(message = "responsibleEntity es obligatoria")
    private String responsibleEntity;

    public ResultadoFinal getFinalResult() { return finalResult; }
    public void setFinalResult(ResultadoFinal finalResult) { this.finalResult = finalResult; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public List<String> getInstitutionalActions() { return institutionalActions; }
    public void setInstitutionalActions(List<String> institutionalActions) { this.institutionalActions = institutionalActions; }

    public String getResponsibleEntity() { return responsibleEntity; }
    public void setResponsibleEntity(String responsibleEntity) { this.responsibleEntity = responsibleEntity; }
}
