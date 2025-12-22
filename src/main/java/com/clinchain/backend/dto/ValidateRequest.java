package com.clinchain.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRequest {
    @NotBlank(message = "Actor is required")
    private String actor;
}
