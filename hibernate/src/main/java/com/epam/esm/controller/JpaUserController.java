package com.epam.esm.controller;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.service.JpaOrderService;
import com.epam.esm.service.JpaService;
import com.epam.esm.service.JpaTagService;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/jpa/user")
@Validated
public class JpaUserController {
    private static final String PARENT_RELATION_NAME = "parent";
    private static final String CERTIFICATE_RELATION_NAME = "giftCertificate";
    private final JpaService<User> userService;
    private final JpaOrderService orderService;
    private final JpaTagService tagService;

    @Autowired
    public JpaUserController(JpaService<User> userService, JpaOrderService orderService, JpaTagService tagService) {
        this.userService = userService;
        this.orderService = orderService;
        this.tagService = tagService;
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = { "application/hal+json" })
    public CollectionModel<User> findAll(@RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(page, size);
        List<User> users = userService.findAll(rph);
        if (users.isEmpty()) {
            throw new NoContentException();
        }
        for (User user : users) {
            Link link = linkTo(methodOn(JpaUserController.class).findById(user.getId()))
                            .withSelfRel();
            user.add(link);
        }
        Link link = linkTo(methodOn(JpaUserController.class).findAll(page, size))
                        .withSelfRel();
        return CollectionModel.of(users, link);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = { "application/hal+json" })
    public User findById(@PathVariable long id) {
        User user = userService.findById(id);
        user.add(linkTo(JpaUserController.class).slash(id).withSelfRel());
        user.add(linkTo(JpaUserController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        user.add(linkTo(methodOn(JpaUserController.class).findUserOrders(id, 0, 5))
                .withRel(IanaLinkRelations.COLLECTION));
        return user;
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/order", produces = { "application/hal+json" })
    public CollectionModel<Order> findUserOrders(@PathVariable long id,
                                                 @RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "5") Integer size) {
        List<Order> orders = orderService.findAllByUserId(id, page, size);
        if (orders.isEmpty()) {
            throw new NoContentException();
        }
        for (Order order : orders) {
            Link link = linkTo(methodOn(JpaUserController.class).findOrderById(id, order.getId())).withSelfRel();
            order.add(link);
        }
        Link selflink = linkTo(methodOn(JpaUserController.class).findUserOrders(id, page, size)).withSelfRel();
        Link userLink = linkTo(methodOn(JpaUserController.class).findById(id))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME));
        return CollectionModel.of(orders, selflink, userLink);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/order/{orderId}", produces = { "application/hal+json" })
    public Order findOrderById(@PathVariable long id,
                               @PathVariable long orderId) {
        Order order = orderService.findByUserAndOrderIds(id, orderId);
        return addLinksToOrder(id, order);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}/order", produces = { "application/hal+json" },
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Order createOrder(@Valid @RequestBody Order order,
                             @PathVariable long id) {
        order.setUserId(id);
        order = orderService.create(order);
        return addLinksToOrder(id, order);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/tag", produces = { "application/hal+json" })
    public Tag findMostWidelyUsedTagWithHighestCostOfAllOrders(@PathVariable long id) {
        Tag tag = tagService.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(id);
        tag.add(linkTo(methodOn(JpaUserController.class)
                .findMostWidelyUsedTagWithHighestCostOfAllOrders(id)).withSelfRel());
        tag.add(linkTo(methodOn(JpaUserController.class).findById(id)).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        return tag;
    }

    private Order addLinksToOrder(long userId, Order order) {
        order.add(linkTo(methodOn(JpaUserController.class).findOrderById(userId, order.getId())).withSelfRel());
        order.add(linkTo(methodOn(JpaUserController.class).findUserOrders(userId, 0, 5))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        order.add(WebMvcLinkBuilder.linkTo(methodOn(JpaGiftCertificateController.class).findById(order.getGiftCertificateId()))
                .withRel(LinkRelation.of(CERTIFICATE_RELATION_NAME)));
        return order;
    }
}
