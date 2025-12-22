package com.clinchain.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddHistoryRequest {
    @NotBlank(message = "Action is required")
    private String action;

    @NotBlank(message = "Actor is required")
    private String actor;
}
