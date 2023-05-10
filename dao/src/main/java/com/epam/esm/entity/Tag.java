package com.epam.esm.entity;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonPropertyOrder({"id", "name"})
public class Tag extends RepresentationModel<Tag> implements Serializable {
    @JsonView(Views.FullView.class)
    private long id;
    @JsonView(Views.ShortView.class)
    @NotBlank(message = "name of tag must not be empty")
//    @JsonProperty("tagName")
    private String name;
}
