package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "`user`")
public class User extends RepresentationModel<User> implements Serializable {
    @JsonView(Views.ShortView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.ShortView.class)
    private String username;

    @JsonView(Views.FullView.class)
    private String email;

    @JsonView(Views.IgnoredView.class)
    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();
}
