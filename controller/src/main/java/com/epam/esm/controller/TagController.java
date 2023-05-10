package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.Service;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tag")
@Validated
public class TagController {

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
     * @throws DaoException
     * @throws ServiceException
     */
    @JsonView(Views.ShortView.class)
    @GetMapping(produces = { "application/hal+json" })
    public ResponseEntity<CollectionModel<Tag>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size) throws DaoException, ServiceException {
        RequestParametersHolder rph = new RequestParametersHolder();
        rph.setPage(page);
        rph.setSize(size);
        List<Tag> tags = tagService.findAll(rph);
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel();
            tag.add(link);
        }

        Link link = linkTo(methodOn(TagController.class).findAll(page, size)).withSelfRel();
        CollectionModel<Tag> collectionModel = CollectionModel.of(tags, link);
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
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
     * @throws DaoException if the {@code Tag} with such {@code id} not found
     * @throws ServiceException
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = { "application/hal+json" })
    public Tag findById(@PathVariable long id) throws DaoException, ServiceException {
        Tag tag = tagService.findById(id);
        tag.add(linkTo(TagController.class).slash(id).withSelfRel());
        tag.add(linkTo(TagController.class).withRel(LinkRelation.of("parent")));
        return tag;
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
     * @param ucb
     * @return The created {@code Tag} with such {@code tagName} with Http Status 201
     * @throws DaoException if the {@code Tag} with such {@code tagName} already exists
     * @throws ServiceException
     */
    @JsonView(Views.FullView.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = { "application/hal+json" })
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag,
                                      BindingResult bindingResult,
                                      UriComponentsBuilder ucb)
            throws DaoException, ServiceException {
        tag = tagService.create(tag);
        tag.add(linkTo(TagController.class).slash(tag.getId()).withSelfRel());
        tag.add(linkTo(TagController.class).withRel(LinkRelation.of("parent")));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/tag/{id}").buildAndExpand(tag.getId()).toUri());
        return new ResponseEntity<>(tag, headers, HttpStatus.CREATED);
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
     * @throws DaoException
     * @throws ServiceException
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}", produces = { "application/hal+json" })
    public RepresentationModel<?> delete(@PathVariable long id) throws DaoException, ServiceException {
        tagService.delete(id);
        return new RepresentationModel<>().add(linkTo(methodOn(TagController.class).findAll(null, null))
                                                .withRel("parent"));
    }

}
