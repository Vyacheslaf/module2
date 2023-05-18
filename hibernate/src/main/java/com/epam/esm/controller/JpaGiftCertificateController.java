package com.epam.esm.controller;

import com.epam.esm.controller.dto.GiftCertificateDuration;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.service.JpaGiftCertificateService;
import com.epam.esm.service.JpaTagService;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa/certificate")
@Validated
public class JpaGiftCertificateController {
    private static final String PARENT_RELATION_NAME = "parent";
    private final JpaGiftCertificateService giftCertificateService;
    private final JpaTagService tagService;

    @Autowired
    public JpaGiftCertificateController(JpaGiftCertificateService giftCertificateService, JpaTagService tagService) {
        this.giftCertificateService = giftCertificateService;
        this.tagService = tagService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(null);
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = { "application/hal+json" })
    public CollectionModel<GiftCertificate> findAll(@RequestParam(required = false) List<String> tag,
                                                    @RequestParam(required = false) String search,
                                                    @RequestParam(required = false) List<String> sort,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(tag, search, sort, page, size);
        List<GiftCertificate> giftCertificates = giftCertificateService.findAll(rph);
        if (giftCertificates.isEmpty()) {
            throw new NoContentException();
        }
        for (GiftCertificate giftCertificate : giftCertificates) {
            Link link = linkTo(methodOn(JpaGiftCertificateController.class).findById(giftCertificate.getId()))
                        .withSelfRel();
            giftCertificate.add(link);
        }
        Link link = linkTo(methodOn(JpaGiftCertificateController.class).findAll(tag, search, sort, page, size))
                .withSelfRel();
        return CollectionModel.of(giftCertificates, link);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = { "application/hal+json" })
    public GiftCertificate findById(@PathVariable long id) {
        return addLinksToGiftCertificate(giftCertificateService.findById(id));
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/tag", produces = { "application/hal+json" })
    public CollectionModel<Tag> findGiftCertificateTags(@PathVariable long id,
                                                        @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "5") Integer size) {
        RequestParametersHolder rph = new RequestParametersHolder(page, size);
        List<Tag> tags = tagService.findGiftCertificateTags(id, rph);
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(JpaTagController.class).findById(tag.getId())).withSelfRel();
            tag.add(link);
        }
        Link selflink = linkTo(methodOn(JpaGiftCertificateController.class).findGiftCertificateTags(id, page, size))
                .withSelfRel();
        Link userLink = linkTo(methodOn(JpaGiftCertificateController.class).findById(id))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME));
        return CollectionModel.of(tags, selflink, userLink);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = { "application/hal+json" })
    public GiftCertificate create(@Valid @RequestBody GiftCertificate certificate) {
        return addLinksToGiftCertificate(giftCertificateService.create(certificate));
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = { "application/hal+json" })
    public GiftCertificate update(@PathVariable long id, @RequestBody GiftCertificate certificate) {
        certificate.setId(id);
        return addLinksToGiftCertificate(giftCertificateService.update(certificate));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}", produces = { "application/hal+json" })
    public void delete(@PathVariable long id) {
        giftCertificateService.delete(id);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{id}/duration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = { "application/hal+json" })
    public GiftCertificate updateDuration(@PathVariable long id,
                                          @Valid @RequestBody GiftCertificateDuration dto) {
        return addLinksToGiftCertificate(giftCertificateService.updateDuration(id, dto.getDuration()));
    }

    private GiftCertificate addLinksToGiftCertificate(GiftCertificate certificate) {
        certificate.add(linkTo(JpaGiftCertificateController.class).slash(certificate.getId()).withSelfRel());
        certificate.add(linkTo(JpaGiftCertificateController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        certificate.add(linkTo(methodOn(JpaGiftCertificateController.class)
                .findGiftCertificateTags(certificate.getId(), null, null))
                .withRel(IanaLinkRelations.COLLECTION));
        return certificate;
    }
}
