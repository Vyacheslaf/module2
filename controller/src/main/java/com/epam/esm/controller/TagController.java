package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.service.Service;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tag")
@Validated
public class TagController {
    private static final String PARENT_RELATION_NAME = "parent";
    private final Service<Tag> tagService;

    @Autowired
    public TagController(Service<Tag> tagService) {
        this.tagService = tagService;
    }

    /**
     * Gets all {@code Tag}s
     *
     * GET /tag:
     *      Request:
     *          No body
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200, 204
     *              Response Body: java.util.List<com.epam.esm.Tag>
     *                  application/json: [{"id":number},{"id":"string"}...]
     *
     * @return List of {@code Tag} with Http Status 200
     *          or empty response with Http Status 204 if any {@code Tag} was not found
     */
    @JsonView(Views.ShortView.class)
    @GetMapping(produces = { "application/hal+json" })
    public CollectionModel<Tag> findAll(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(page, size);
        List<Tag> tags = tagService.findAll(rph);
        if (tags.isEmpty()) {
            throw new NoContentException();
        }
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel();
            tag.add(link);
        }
        Link link = linkTo(methodOn(TagController.class).findAll(page, size)).withSelfRel();
        return CollectionModel.of(tags, link);
    }

    /**
     * Gets {@code Tag} by its {@code id}
     *
     * GET /tag/{id}:
     *      Request:
     *          No body
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200
     *              Response Body: com.epam.esm.Tag
     *                  application/json: {"id":number,"tagName":"string"}
     *
     * @param id {@code Tag}'s ID
     * @see com.epam.esm.entity.Tag
     * @return The {@code Tag} with such {@code id} with Http Status 200
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = { "application/hal+json" })
    public Tag findById(@PathVariable long id) {
        return addLinksToTag(tagService.findById(id));
    }

    /**
     * Create {@code Tag} with {@code name}
     *
     * POST /tag:
     *      Request:
     *          Content-Type: application/json
     *          Request Body: com.epam.esm.Tag
     *              application/json: {"tagName":"string"}
     *
     *      Response:
     *          Location: /tag/{id}
     *          Content-Type: application/json
     *          Status Codes: 201
     *              Response Body: com.epam.esm.Tag
     *                  application/json: {"id":number,"tagName":"string"}
     *
     * @param tag Created {@code Tag}
     * @see com.epam.esm.entity.Tag
     * @param bindingResult
     * param ucb
     * @return The created {@code Tag} with such {@code tagName} with Http Status 201
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = { "application/hal+json" })
    public Tag create(@Valid @RequestBody Tag tag, BindingResult bindingResult) {
        return addLinksToTag(tagService.create(tag));
    }

    /**
     * Removes {@code Tag} by its {@code id}
     *
     * DELETE /tag/{id}:
     *      Request:
     *          No body
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200
     *              Response Body:
     *                  No body
     *
     * @param id {@code Tag}'s {@code id}
     * @see com.epam.esm.entity.Tag
     * @return Nothing with Http Status 200
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}", produces = { "application/hal+json" })
    public RepresentationModel<?> delete(@PathVariable long id) {
        tagService.delete(id);
        return new RepresentationModel<>().add(linkTo(methodOn(TagController.class).findAll(null, null))
                                                .withRel(PARENT_RELATION_NAME));
    }

    private Tag addLinksToTag(Tag tag) {
        tag.add(linkTo(TagController.class).slash(tag.getId()).withSelfRel());
        tag.add(linkTo(TagController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        return tag;
    }
}
