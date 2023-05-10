package com.epam.esm.entity;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class Order extends RepresentationModel<Order> implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    @JsonView(Views.ShortView.class)
    private long id;
    @JsonView(Views.IgnoredView.class)
    private long userId;
    @NotNull
    @JsonView(Views.FullView.class)
    private long giftCertificateId;
    @JsonView(Views.FullView.class)
    private int cost;
    @JsonView(Views.FullView.class)
    @JsonFormat(pattern = ISO_8601_PATTERN)
    private LocalDateTime purchaseDate;
}
