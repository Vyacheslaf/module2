package com.epam.esm.dao.sql;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.exception.InvalidSortRequestException;
import com.epam.esm.exception.InvalidTagNameException;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoWrongIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao {
    private static final String CREATE_QUERY = "INSERT INTO gift_certificate VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT gc.*, t.id AS tag_id, t.name AS tag_name " +
                                                   "FROM gift_certificate gc " +
                                                   "LEFT JOIN gift_certificate_tag gct " +
                                                        "ON gc.id = gct.gift_certificate_id " +
                                                   "LEFT JOIN tag t ON gct.tag_id = t.id " +
                                                   "WHERE gc.id = ? ";
    private static final String FIND_ALL_QUERY = "SELECT gc.id, gc.name FROM gift_certificate gc ";
    private static final String TAGS_QUERY_FIRST_PART = "gc.id IN (SELECT gct.gift_certificate_id " +
                                                            "FROM gift_certificate_tag gct " +
                                                            "LEFT JOIN tag t ON t.id = gct.tag_id WHERE t.name IN ";
    private static final String TAGS_QUERY_LAST_PART = "GROUP BY gct.gift_certificate_id HAVING COUNT(*) = ?) ";
    private static final String SEARCH_QUERY = "(CONCAT(gc.name, ' ', gc.description) LIKE ?) ";
    private static final String WHERE_PREFIX = "WHERE ";
    private static final String AND_DELIMITER = "AND ";
    private static final String ORDER_BY_QUERY = "ORDER BY ";
    private static final String LIMIT_OFFSET = "LIMIT ? OFFSET ?";
    private static final String CHECK_IF_CERTIFICATE_EXIST_QUERY = "SELECT COUNT(1) FROM gift_certificate WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE gift_certificate " +
                                               "SET name = COALESCE(?, name), " +
                                                   "description = COALESCE(?, description), " +
                                                   "price=COALESCE(?, price), " +
                                                   "duration = COALESCE(?, duration), " +
                                                   "create_date = COALESCE(?, create_date), " +
                                                   "last_update_date = COALESCE(?, last_update_date) " +
                                               "WHERE id = ?";
    private static final String UPDATE_DURATION_QUERY = "UPDATE gift_certificate " +
                                                        "SET duration = ?, last_update_date = ? " +
                                                        "WHERE id = ?";
    private static final String CREATE_CERTIFICATE_TAG_QUERY = "INSERT INTO gift_certificate_tag VALUES (?, ?)";
    private static final String DELETE_CERTIFICATE_TAG_QUERY = "DELETE FROM gift_certificate_tag " +
                                                               "WHERE gift_certificate_id = ?";
    private static final String CREATE_TAG_QUERY = "INSERT INTO tag VALUES (DEFAULT, ?) " +
                                                   "ON DUPLICATE KEY UPDATE tag.id = tag.id";
    private static final String GET_TAG_ID_BY_TAG_NAME_QUERY = "SELECT tag.id FROM tag WHERE tag.name = ?";
    private static final String DELETE_CERTIFICATE_BY_ID_QUERY = "DELETE FROM gift_certificate WHERE id = ?";
    private static final String GIFT_CERTIFICATE_TABLE_SHORT_NAME = "gc.";
    private static final String SQL_ANY_SYMBOL = "%";
    private static final String COMMA_SPACE_DELIMITER = ", ";
    private static final String SPACE_DELIMITER = " ";
    private static final String SPACE_SUFFIX = " ";
    private static final String BRACKET_SPACE_SUFFIX = ") ";
    private static final String BRACKET_PREFIX = "(";
    private static final String QUESTION_MARK = "?";
    private static final String SORT_PATTERN = "^((name|create_date|last_update_date).(asc|desc)){1}$";
    private static final String SORT_REPLACE_PATTERN = "\\.";
    private static final String RESOURCE_NAME = "GiftCertificate";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftCertificateDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public GiftCertificate create(GiftCertificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            pstmt.setString(++k, certificate.getName());
            pstmt.setString(++k, certificate.getDescription());
            pstmt.setInt(++k, certificate.getPrice());
            pstmt.setInt(++k, certificate.getDuration());
            pstmt.setTimestamp(++k, Timestamp.valueOf(certificate.getCreateDate()));
            pstmt.setTimestamp(++k, Timestamp.valueOf(certificate.getLastUpdateDate()));
            return pstmt;
        }, keyHolder);
        certificate.setId(keyHolder.getKey().longValue());
        addTags(certificate);
        return certificate;
    }

    @Override
    public GiftCertificate findById(long id) {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new BeanPropertyRowMapper<>(GiftCertificate.class), id)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongIdException(e, id, RESOURCE_NAME);
        }
    }

    @Override
    public List<GiftCertificate> findAll(RequestParametersHolder rph) {
        if (rph == null) {
            rph = new RequestParametersHolder();
        }
        StringBuilder query = new StringBuilder(FIND_ALL_QUERY);
        List<Object> argsList = new ArrayList<>();
        StringJoiner joiner = new StringJoiner(AND_DELIMITER, WHERE_PREFIX, SPACE_SUFFIX);
        joiner.setEmptyValue("");
        if ((rph.getTags() != null) && !rph.getTags().isEmpty()) {
            joiner.add(prepareTagsQuery(rph.getTags(), argsList));
        }
        if ((rph.getSearch() != null) && !rph.getSearch().isEmpty()) {
            joiner.add(getSearchQuery(rph.getSearch(), argsList));
        }
        query.append(joiner.toString());
        query.append(getOrderByQuery(rph));
        query.append(LIMIT_OFFSET);
        argsList.add(rph.getSize());
        argsList.add(rph.getOffset());
        return jdbcTemplate.query(query.toString(),
                                  new BeanPropertyRowMapper<>(GiftCertificate.class),
                                  argsList.toArray());
    }

    private String getSearchQuery(String search, List<Object> argsList) {
        argsList.add(SQL_ANY_SYMBOL + search + SQL_ANY_SYMBOL);
        return SEARCH_QUERY;
    }

    private String prepareTagsQuery(List<String> tags, List<Object> argsList) {
        StringJoiner joiner = new StringJoiner(COMMA_SPACE_DELIMITER, BRACKET_PREFIX, BRACKET_SPACE_SUFFIX);
        for (String tagName : tags) {
            if ((tagName == null) || tagName.isEmpty()) {
                throw new InvalidTagNameException();
            }
            joiner.add(QUESTION_MARK);
            argsList.add(tagName);
        }
        argsList.add(tags.size());
        return TAGS_QUERY_FIRST_PART + joiner.toString() + TAGS_QUERY_LAST_PART;
    }

    private String getOrderByQuery(RequestParametersHolder rph) {
        if ((rph.getSortList() == null) || rph.getSortList().isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(COMMA_SPACE_DELIMITER, ORDER_BY_QUERY, SPACE_DELIMITER);
        for (String sort : rph.getSortList()) {
            if ((sort == null) || !sort.matches(SORT_PATTERN)) {
                throw new InvalidSortRequestException(SORT_PATTERN.substring(2, SORT_PATTERN.length() - 5));
            }
            joiner.add(GIFT_CERTIFICATE_TABLE_SHORT_NAME + sort.replaceFirst(SORT_REPLACE_PATTERN, SPACE_DELIMITER));
        }
        return joiner.toString();
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate certificate) {
        if (jdbcTemplate.queryForObject(CHECK_IF_CERTIFICATE_EXIST_QUERY, Long.class, certificate.getId()) == 0) {
            throw new DaoWrongIdException(certificate.getId(), RESOURCE_NAME);
        }
        Object[] args = new Object[] {certificate.getName(),
                certificate.getDescription(),
                certificate.getPrice(),
                certificate.getDuration(),
                convertLocalDateTimeToTimestamp(certificate.getCreateDate()),
                convertLocalDateTimeToTimestamp(certificate.getLastUpdateDate()),
                certificate.getId()};
        jdbcTemplate.update(UPDATE_QUERY, args);
        if (!certificate.getTags().isEmpty()) {
            jdbcTemplate.update(DELETE_CERTIFICATE_TAG_QUERY, certificate.getId());
            addTags(certificate);
        }
        certificate = findById(certificate.getId());
        return certificate;
    }

    @Override
    @Transactional
    public GiftCertificate updateDuration(long id, int duration, LocalDateTime currentDate) {
        if (jdbcTemplate.queryForObject(CHECK_IF_CERTIFICATE_EXIST_QUERY, Long.class, id) == 0) {
            throw new DaoWrongIdException(id, RESOURCE_NAME);
        }
//        Object[] args = new Object[] {duration, convertLocalDateTimeToTimestamp(currentDate),id};
        jdbcTemplate.update(UPDATE_DURATION_QUERY, duration, convertLocalDateTimeToTimestamp(currentDate),id);
        return findById(id);
    }

    private Timestamp convertLocalDateTimeToTimestamp (LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private void addTags(GiftCertificate certificate) {
        for (Tag tag : certificate.getTags()) {
            jdbcTemplate.update(CREATE_TAG_QUERY, tag.getName());
            tag.setId(jdbcTemplate.queryForObject(GET_TAG_ID_BY_TAG_NAME_QUERY, Long.class, tag.getName()));
            jdbcTemplate.update(CREATE_CERTIFICATE_TAG_QUERY, certificate.getId(), tag.getId());
        }
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_CERTIFICATE_BY_ID_QUERY, id);
    }
}
