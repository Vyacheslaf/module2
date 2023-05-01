package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class GiftCertificate implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Integer price;
    @NotNull
    private Integer duration;
    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdateDate;
    @Valid
    private Set<Tag> tags = new LinkedHashSet<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }
}
