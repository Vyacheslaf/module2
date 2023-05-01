package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.controller.InvalidRequestException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.Service;
import com.epam.esm.util.RequestParametersHolder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<Tag>> findAll() throws DaoException, ServiceException {
        List<Tag> tags = tagService.findAll(new RequestParametersHolder());
        if (tags.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tags, HttpStatus.OK);
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
    @GetMapping("/{id}")
    public ResponseEntity<Tag> findById(@PathVariable long id) throws DaoException, ServiceException {
        return new ResponseEntity<>(tagService.findById(id), HttpStatus.OK);
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag,
                                      BindingResult bindingResult,
                                      UriComponentsBuilder ucb)
            throws DaoException, ServiceException {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException("Field tagName cannot be null");
        }
        tag = tagService.create(tag);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Tag> delete(@PathVariable long id) throws DaoException, ServiceException {
        tagService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
