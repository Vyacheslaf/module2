package com.epam.esm.controller;

import com.epam.esm.controller.dto.GiftCertificateDuration;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.util.*;
import com.epam.esm.entity.GiftCertificate;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/certificate")
@Validated
public class GiftCertificateController {
    private static final String PARENT_RELATION_NAME = "parent";
    private final GiftCertificateService giftCertificateService;
    private final TagService tagService;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService, TagService tagService) {
        this.giftCertificateService = giftCertificateService;
        this.tagService = tagService;
    }

    /**
     * Gets all {@code GiftCertificate} by its {@code id}
     * <p>
     * GET /certificate/{id}:
     * Request:
     * Request Body:
     * No body
     * Request Parameters:
     * (optional) tagName: any string
     * (optional) search: any string
     * (optional, multiple) sortBy: "name" | "date" (case insensitive)
     * (optional, multiple) sortDir: "asc" | "desc" (case insensitive)
     * <p>
     * Response:
     * Content-Type: application/json
     * Status Codes: 200, 204
     * Response Body: com.epam.esm.GiftCertificate
     * application/json: [{"id":number,
     * "name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * "createDate":"string",
     * "lastUpdateDate":"string",
     * "tags":[{"id":number,"tagName":"string"}
     * ,{"id":number,"tagName":"string"},...]},
     * {"id":number,
     * "name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * "createDate":"string",
     * "lastUpdateDate":"string",
     * "tags":[{"id":number,"tagName":"string"},
     * {"id":number,"tagName":"string"},...]},
     * ...]
     *
     * @param tag    Request parameter for search by {@code Tag.tagName}
     * @param search Request parameter for search by part of {@code GiftCertificate.name}
     *               or {@code GiftCertificate.description}
     *               param sortBy Request parameter for sort by {@code GiftCertificate.name} or {@code GiftCertificate.createDate}
     *               param sortDir Request parameter for select sort direction {@code asc} | {@code | desc}
     * @return List of {@code GiftCertificate} with Http Status 200
     * or empty response with Http Status 204 if any {@code Tag} was not found
     */
    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = {"application/hal+json"})
    public CollectionModel<GiftCertificate> findAll(@RequestParam(required = false) List<String> tag,
                                                    @RequestParam(required = false) String search,
//            @Pattern(regexp = SORT_PATTERN, message = SORT_REGEX_ERROR_MESSAGE)
                                                    @RequestParam(required = false) List<String> sort,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(tag, search, sort, page, size);
        List<GiftCertificate> giftCertificates = giftCertificateService.findAll(rph);
        if (giftCertificates.isEmpty()) {
            throw new NoContentException();
        }
        for (GiftCertificate giftCertificate : giftCertificates) {
            Link link = linkTo(methodOn(GiftCertificateController.class).findById(giftCertificate.getId()))
                    .withSelfRel();
            giftCertificate.add(link);
        }
        Link link = linkTo(methodOn(GiftCertificateController.class).findAll(tag, search, sort, page, size))
                .withSelfRel();
        return CollectionModel.of(giftCertificates, link);
    }

    /**
     * Gets {@code GiftCertificate} by its {@code id}
     * <p>
     * GET /certificate/{id}:
     * Request:
     * No body
     * <p>
     * Response:
     * Content-Type: application/json
     * Status Codes: 200
     * Response Body: com.epam.esm.GiftCertificate
     * application/json: {"id":number,
     * "name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * "createDate":"string",
     * "lastUpdateDate":"string",
     * "tags":[{"id":"string","tagName":"string"},
     * {"id":"string","tagName":"string"},...]}
     * <p>
     * Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param id {@code GiftCertificate}'s {@code id}
     * @return The {@code GiftCertificate} with such {@code id} with Http Status 200
     * @see com.epam.esm.entity.GiftCertificate
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = {"application/hal+json"})
    public GiftCertificate findById(@PathVariable long id) {
        return addLinksToGiftCertificate(giftCertificateService.findById(id));
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/tag", produces = {"application/hal+json"})
    public CollectionModel<Tag> findGiftCertificateTags(@PathVariable long id,
                                                        @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(page, size);
        List<Tag> tags = tagService.findGiftCertificateTags(id, rph);
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel();
            tag.add(link);
        }
        Link selflink = linkTo(methodOn(GiftCertificateController.class).findGiftCertificateTags(id, page, size))
                .withSelfRel();
        Link userLink = linkTo(methodOn(GiftCertificateController.class).findById(id))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME));
        return CollectionModel.of(tags, selflink, userLink);
    }

    /**
     * Create {@code GiftCertificate}
     * <p>
     * POST /certificate:
     * Request:
     * Content-Type: application/json
     * Request Body: com.epam.esm.GiftCertificate
     * application/json: {"name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * Optional "createDate":"string",
     * Optional "lastUpdateDate":"string",
     * Optional "tags":[{"tagName":"string"},{"tagName":"string"},...]}
     * <p>
     * Response:
     * Location: /certificate/{id}
     * Content-Type: application/json
     * Status Codes: 201
     * Response Body: com.epam.esm.GiftCertificate
     * application/json: {"id":number,
     * "name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * "createDate":"string",
     * "lastUpdateDate":"string",
     * "tags":[{"id":number,"tagName":"string"},
     * {"id":number,"tagName":"string"},...]}
     * <p>
     * Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param certificate   Created {@code GiftCertificate} with required fields: name, description, price, duration
     * @param bindingResult param ucb
     * @return The created {@code GiftCertificate} with Http Status 201
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/hal+json"})
    public GiftCertificate create(@Valid @RequestBody GiftCertificate certificate, BindingResult bindingResult) {
        return addLinksToGiftCertificate(giftCertificateService.create(certificate));
    }

    /**
     * Update {@code GiftCertificate} with selected {@code id}
     * <p>
     * PATCH /certificate/{id}:
     * Request:
     * Content-Type: application/json
     * Request Body: com.epam.esm.GiftCertificate
     * application/json: {Optional "name":"string",
     * Optional "description":"string",
     * Optional "price":number,
     * Optional "duration":number,
     * Optional "createDate":"string",
     * Optional "lastUpdateDate":"string",
     * Optional "tags":[{"tagName":"string"},{"tagName":"string"},...]}
     * <p>
     * Response:
     * Location: /certificate/{id}
     * Content-Type: application/json
     * Status Codes: 201
     * Response Body: com.epam.esm.GiftCertificate
     * application/json: {"id":number,
     * "name":"string",
     * "description":"string",
     * "price":number,
     * "duration":number,
     * "createDate":"string",
     * "lastUpdateDate":"string",
     * "tags":[{"id":number,"tagName":"string"},
     * {"id":number,"tagName":"string"},...]}
     * <p>
     * Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param certificate Created {@code GiftCertificate} with required fields: name, description, price, duration
     * @param id          {@code GiftCertificate}'s {@code id}
     * @param certificate Update {@code GiftCertificate} with all optional fields
     *                    param ucb
     * @return The updated {@code GiftCertificate} with Http Status 200
     */
    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/hal+json"})
    public GiftCertificate update(@PathVariable long id, @RequestBody GiftCertificate certificate) {
        certificate.setId(id);
        return addLinksToGiftCertificate(giftCertificateService.update(certificate));
    }

    /**
     * Removes {@code GiftCertificate} by its {@code id}
     * <p>
     * DELETE /certificate/{id}:
     * Request:
     * No body
     * <p>
     * Response:
     * Content-Type: application/json
     * Status Codes: 200
     * Response Body:
     * No body
     *
     * @param id {@code GiftCertificate}'s {@code id}
     * @return Nothing with Http Status 200
     * @see com.epam.esm.entity.GiftCertificate
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}", produces = {"application/hal+json"})
    public RepresentationModel<?> delete(@PathVariable long id) {
        giftCertificateService.delete(id);
        return new RepresentationModel<>()
                .add(linkTo(methodOn(GiftCertificateController.class)
                        .findAll(null, null, null, null, null))
                        .withRel(PARENT_RELATION_NAME));
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{id}/duration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {"application/hal+json"})
    public GiftCertificate updateDuration(@PathVariable long id,
                                          @Valid @RequestBody GiftCertificateDuration dto,
                                          BindingResult bindingResult) {
        return addLinksToGiftCertificate(giftCertificateService.updateDuration(id, dto.getDuration()));
    }

    private GiftCertificate addLinksToGiftCertificate(GiftCertificate certificate) {
        certificate.add(linkTo(GiftCertificateController.class).slash(certificate.getId()).withSelfRel());
        certificate.add(linkTo(GiftCertificateController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        certificate.add(linkTo(methodOn(GiftCertificateController.class)
                .findGiftCertificateTags(certificate.getId(), null, null))
                .withRel(IanaLinkRelations.COLLECTION));
        return certificate;
    }
}
