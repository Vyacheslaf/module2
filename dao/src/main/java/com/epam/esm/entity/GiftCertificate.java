package com.epam.esm.entity;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @JsonView(Views.ShortView.class)
    private long id;

    @JsonView(Views.ShortView.class)
    @NotBlank(message = "name of gift certificate must not be empty")
    private String name;

    @NotBlank(message = "description must not be empty")
    @JsonView(Views.FullView.class)
    private String description;

    @NotNull(message = "price must not be null")
    @JsonView(Views.FullView.class)
    private Integer price;

    @NotNull(message = "duration must not be null")
    @JsonView(Views.FullView.class)
    private Integer duration;

    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonView(Views.FullView.class)
    private LocalDateTime createDate;

    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonView(Views.FullView.class)
    private LocalDateTime lastUpdateDate;

    @Valid
    @JsonView(Views.IgnoredView.class)
    private Set<Tag> tags;// = new LinkedHashSet<>();

/*    public void addTag(Tag tag) {
        this.tags.add(tag);
    }*/
}
