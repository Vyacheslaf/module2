package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@DynamicUpdate
@Table(name = "gift_certificate")
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @JsonView(Views.ShortView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonView(Views.FullView.class)
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Valid
    @JsonView(Views.IgnoredView.class)
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "gift_certificate_tag",
            joinColumns = { @JoinColumn(name = "gift_certificate_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    private Set<Tag> tags;

    @JsonIgnore
    @OneToMany(mappedBy = "giftCertificate")
    private List<Order> orders;

    public GiftCertificate(long id) {
        this.id = id;
    }
}
