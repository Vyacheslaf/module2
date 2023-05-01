package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {
    private long id;
    @NotNull
    @JsonProperty("tagName")
    private String name;
}
