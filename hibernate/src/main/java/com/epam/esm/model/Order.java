package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "`order`")
public class Order extends RepresentationModel<Order> implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @JsonView(Views.ShortView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @JsonView(Views.FullView.class)
    @Column(name = "user_id")
    private long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gift_certificate_id", nullable = false, insertable = false, updatable = false)
    private GiftCertificate giftCertificate;

    @NotNull
    @JsonView(Views.FullView.class)
    @Column(name = "gift_certificate_id")
    private long giftCertificateId;

    @JsonView(Views.FullView.class)
    private int cost;

    @JsonView(Views.FullView.class)
    @JsonFormat(pattern = ISO_8601_PATTERN)
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
}
