package com.epam.esm.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftCertificateDuration {
    @NotNull(message = "duration must not be null")
    private Integer duration;
}
