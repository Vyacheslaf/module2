package com.epam.esm.controller;

import com.epam.esm.exception.controller.InvalidRequestException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.Service;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.util.SortBy;
import com.epam.esm.util.SortDir;
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
@RequestMapping("/certificate")
@Validated
public class GiftCertificateController {
    private final Service<GiftCertificate> giftCertificateService;

    @Autowired
    public GiftCertificateController(Service<GiftCertificate> giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    /**
     * Gets all {@code GiftCertificate} by its {@code id}
     *
     * GET /certificate/{id}:
     *      Request:
     *          Request Body:
     *              No body
     *          Request Parameters:
     *              (optional) tagName: any string
     *              (optional) search: any string
     *              (optional, multiple) sortBy: "name" | "date" (case insensitive)
     *              (optional, multiple) sortDir: "asc" | "desc" (case insensitive)
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200, 204
     *              Response Body: com.epam.esm.GiftCertificate
     *                  application/json: [{"id":number,
     *                                      "name":"string",
     *                                      "description":"string",
     *                                      "price":number,
     *                                      "duration":number,
     *                                      "createDate":"string",
     *                                      "lastUpdateDate":"string",
     *                                      "tags":[{"id":number,"tagName":"string"}
     *                                              ,{"id":number,"tagName":"string"},...]},
     *                                     {"id":number,
     *                                      "name":"string",
     *                                      "description":"string",
     *                                      "price":number,
     *                                      "duration":number,
     *                                      "createDate":"string",
     *                                      "lastUpdateDate":"string",
     *                                      "tags":[{"id":number,"tagName":"string"},
     *                                              {"id":number,"tagName":"string"},...]},
     *                                      ...]
     *
     * @param tagName Request parameter for search by {@code Tag.tagName}
     * @param search Request parameter for search by part of {@code GiftCertificate.name}
     *               or {@code GiftCertificate.description}
     * @param sortBy Request parameter for sort by {@code GiftCertificate.name} or {@code GiftCertificate.createDate}
     * @param sortDir Request parameter for select sort direction {@code asc} | {@code | desc}
     * @return List of {@code GiftCertificate} with Http Status 200
     *          or empty response with Http Status 204 if any {@code Tag} was not found
     * @throws DaoException
     * @throws ServiceException
     */
    @GetMapping
    public ResponseEntity<List<GiftCertificate>> findAll(@RequestParam(required = false) String tagName,
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(required = false) List<SortBy> sortBy,
                                                         @RequestParam(required = false) List<SortDir> sortDir)
            throws ServiceException, DaoException {
        RequestParametersHolder rph = new RequestParametersHolder(tagName, search, sortBy, sortDir);
        List<GiftCertificate> giftCertificates = giftCertificateService.findAll(rph);
        if (giftCertificates.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(giftCertificates, HttpStatus.OK);
    }

    /**
     * Gets {@code GiftCertificate} by its {@code id}
     *
     * GET /certificate/{id}:
     *      Request:
     *          No body
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200
     *              Response Body: com.epam.esm.GiftCertificate
     *                  application/json: {"id":number,
     *                                     "name":"string",
     *                                     "description":"string",
     *                                     "price":number,
     *                                     "duration":number,
     *                                     "createDate":"string",
     *                                     "lastUpdateDate":"string",
     *                                     "tags":[{"id":"string","tagName":"string"},
     *                                             {"id":"string","tagName":"string"},...]}
     *
     *  Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param id {@code GiftCertificate}'s {@code id}
     * @see com.epam.esm.entity.GiftCertificate
     * @return The {@code GiftCertificate} with such {@code id} with Http Status 200
     * @throws DaoException if the {@code GiftCertificate} with such {@code id} not found
     * @throws ServiceException
     */
    @GetMapping("/{id}")
    public ResponseEntity<GiftCertificate> findById(@PathVariable long id)
            throws ServiceException, DaoException {
        GiftCertificate certificate = giftCertificateService.findById(id);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }

    /**
     * Create {@code GiftCertificate}
     *
     * POST /certificate:
     *      Request:
     *          Content-Type: application/json
     *          Request Body: com.epam.esm.GiftCertificate
     *              application/json: {"name":"string",
     *                                 "description":"string",
     *                                 "price":number,
     *                                 "duration":number,
     *                                 Optional "createDate":"string",
     *                                 Optional "lastUpdateDate":"string",
     *                                 Optional "tags":[{"tagName":"string"},{"tagName":"string"},...]}
     *
     *      Response:
     *          Location: /certificate/{id}
     *          Content-Type: application/json
     *          Status Codes: 201
     *              Response Body: com.epam.esm.GiftCertificate
     *                  application/json: {"id":number,
     *                                     "name":"string",
     *                                     "description":"string",
     *                                     "price":number,
     *                                     "duration":number,
     *                                     "createDate":"string",
     *                                     "lastUpdateDate":"string",
     *                                     "tags":[{"id":number,"tagName":"string"},
     *                                             {"id":number,"tagName":"string"},...]}
     *
     *  Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param certificate Created {@code GiftCertificate} with required fields: name, description, price, duration
     *
     * @param bindingResult
     * @param ucb
     * @return The created {@code GiftCertificate} with Http Status 201
     * @throws ServiceException
     * @throws DaoException
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftCertificate> create(@Valid @RequestBody GiftCertificate certificate,
                                                  BindingResult bindingResult,
                                                  UriComponentsBuilder ucb)
            throws ServiceException, DaoException {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        certificate = giftCertificateService.create(certificate);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/certificate/{id}").buildAndExpand(certificate.getId()).toUri());
        return new ResponseEntity<>(certificate, headers, HttpStatus.CREATED);
    }

    /**
     * Update {@code GiftCertificate} with selected {@code id}
     *
     * PUT /certificate/{id}:
     *      Request:
     *          Content-Type: application/json
     *          Request Body: com.epam.esm.GiftCertificate
     *              application/json: {Optional "name":"string",
     *                                 Optional "description":"string",
     *                                 Optional "price":number,
     *                                 Optional "duration":number,
     *                                 Optional "createDate":"string",
     *                                 Optional "lastUpdateDate":"string",
     *                                 Optional "tags":[{"tagName":"string"},{"tagName":"string"},...]}
     *
     *      Response:
     *          Location: /certificate/{id}
     *          Content-Type: application/json
     *          Status Codes: 201
     *              Response Body: com.epam.esm.GiftCertificate
     *                  application/json: {"id":number,
     *                                     "name":"string",
     *                                     "description":"string",
     *                                     "price":number,
     *                                     "duration":number,
     *                                     "createDate":"string",
     *                                     "lastUpdateDate":"string",
     *                                     "tags":[{"id":number,"tagName":"string"},
     *                                             {"id":number,"tagName":"string"},...]}
     *
     *  Fields "createDate" and "lastUpdateDate" are in format "yyyy-MM-dd'T'HH:mm:ss.SSS".
     *
     * @param certificate Created {@code GiftCertificate} with required fields: name, description, price, duration
     *
     * @param id {@code GiftCertificate}'s {@code id}
     * @param certificate Update {@code GiftCertificate} with all optional fields
     * @param ucb
     * @return The updated {@code GiftCertificate} with Http Status 200
     * @throws ServiceException
     * @throws DaoException
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftCertificate> update(@PathVariable long id,
                                                  @RequestBody GiftCertificate certificate,
                                                  UriComponentsBuilder ucb)
            throws ServiceException, DaoException {
        certificate.setId(id);
        certificate = giftCertificateService.update(certificate);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/certificate/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<>(certificate, headers, HttpStatus.OK);
    }

    /**
     * Removes {@code GiftCertificate} by its {@code id}
     *
     * DELETE /certificate/{id}:
     *      Request:
     *          No body
     *
     *      Response:
     *          Content-Type: application/json
     *          Status Codes: 200
     *              Response Body:
     *                  No body
     *
     * @param id {@code GiftCertificate}'s {@code id}
     * @see com.epam.esm.entity.GiftCertificate
     * @return Nothing with Http Status 200
     * @throws DaoException
     * @throws ServiceException
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<GiftCertificate> delete(@PathVariable long id) throws ServiceException, DaoException {
        giftCertificateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
