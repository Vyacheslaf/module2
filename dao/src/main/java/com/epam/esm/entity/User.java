package com.epam.esm.entity;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User extends RepresentationModel<User> implements Serializable {
    @JsonView(Views.ShortView.class)
    private long id;
    @JsonView(Views.ShortView.class)
    private String username;
    @JsonView(Views.FullView.class)
    private String email;
    @JsonView(Views.IgnoredView.class)
    private List<Order> orders;
}
