package com.epam.esm.dao.sql;

import com.epam.esm.dao.Dao;
import com.epam.esm.util.RequestParametersHolder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.util.SortDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequestScope
public class GiftCertificateDao implements Dao<GiftCertificate> {
    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_QUERY = "INSERT INTO gift_certificate VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT gc.*, t.id AS tag_id, t.name AS tag_name " +
                                                   "FROM gift_certificate gc " +
                                                   "LEFT JOIN gift_certificate_tag gct " +
                                                        "ON gc.id = gct.gift_certificate_id " +
                                                   "LEFT JOIN tag t ON gct.tag_id = t.id " +
                                                   "WHERE gc.id = ? ";
    private static final String SQL_ANY_SYMBOL = "%";
    private static final String FIND_ALL_QUERY = "SELECT gc.*, t.id AS tag_id, t.name AS tag_name " +
                                                 "FROM gift_certificate gc " +
                                                 "LEFT JOIN gift_certificate_tag gct " +
                                                 "ON gc.id = gct.gift_certificate_id " +
                                                 "LEFT JOIN tag t ON gct.tag_id = t.id " +
                                                 "WHERE ((gc.id IN (SELECT gct.gift_certificate_id " +
                                                                 "FROM gift_certificate_tag gct " +
                                                                 "LEFT JOIN tag t ON gct.tag_id = t.id " +
                                                                 "WHERE t.name = COALESCE(?, t.name))) " +
                                                    "OR IFNULL(?, gc.id IN (SELECT gc.id FROM gift_certificate gc)))" +
                                                 "AND (CONCAT(gc.name, ' ', gc.description) LIKE ?) ";
    private static final String ORDER_BY_QUERY = "ORDER BY ";
    private static final String ORDER_BY_DELIMITER = ", ";
    private static final String CHECK_IF_CERTIFICATE_EXIST_QUERY = "SELECT COUNT(1) FROM gift_certificate WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE gift_certificate " +
                                               "SET name = COALESCE(?, name), " +
                                                   "description = COALESCE(?, description), " +
                                                   "price=COALESCE(?, price), " +
                                                   "duration = COALESCE(?, duration), " +
                                                   "create_date = COALESCE(?, create_date), " +
                                                   "last_update_date = COALESCE(?, last_update_date) " +
                                               "WHERE id = ?";
    private static final String CREATE_CERTIFICATE_TAG_QUERY = "INSERT INTO gift_certificate_tag VALUES (?, ?)";
    private static final String DELETE_CERTIFICATE_TAG_QUERY = "DELETE FROM gift_certificate_tag " +
                                                               "WHERE gift_certificate_id = ?";
    private static final String CREATE_TAG_QUERY = "INSERT INTO tag VALUES (DEFAULT, ?) " +
                                                   "ON DUPLICATE KEY UPDATE tag.id = tag.id";
    private static final String GET_TAG_ID_BY_TAG_NAME_QUERY = "SELECT tag.id FROM tag WHERE tag.name = ?";
    private static final String DELETE_CERTIFICATE_BY_ID_QUERY = "DELETE FROM gift_certificate WHERE id = ?";
    private static final String CERTIFICATE_ID_COLUMN_NAME = "id";
    private static final String CERTIFICATE_NAME_COLUMN_NAME = "name";
    private static final String CERTIFICATE_DESCRIPTION_COLUMN_NAME = "description";
    private static final String CERTIFICATE_PRICE_COLUMN_NAME = "price";
    private static final String CERTIFICATE_DURATION_COLUMN_NAME = "duration";
    private static final String CERTIFICATE_CREATE_DATE_COLUMN_NAME = "create_date";
    private static final String CERTIFICATE_LAST_UPDATE_DATE_COLUMN_NAME = "last_update_date";
    private static final String TAG_NAME_COLUMN_NAME = "tag_name";
    private static final String TAG_ID_COLUMN_NAME = "tag_id";
    private static final String RESOURCE_NAME = "GiftCertificate";

    @Autowired
    public GiftCertificateDao(DataSource dataSource) {
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
    public GiftCertificate findById(long id) throws DaoWrongIdException {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new ListGiftCertificateExtractor(), id)
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
        String search = rph.getSearch() != null ? SQL_ANY_SYMBOL + rph.getSearch() + SQL_ANY_SYMBOL : SQL_ANY_SYMBOL;
        String query = FIND_ALL_QUERY + getOrderByQuery(rph);
        return jdbcTemplate.query(query, new ListGiftCertificateExtractor(), rph.getTagName(), rph.getTagName(), search);
    }

    private String getOrderByQuery(RequestParametersHolder rph) {
        if ((rph.getSortByList() == null) || rph.getSortByList().isEmpty()) {
            return "";
        }
        if (rph.getSortDirList() == null) {
            rph.setSortDirList(new ArrayList<>());
        }
        StringJoiner joiner = new StringJoiner(ORDER_BY_DELIMITER);
        for (int i = 0; i < rph.getSortByList().size(); i++) {
            rph.getSortDirList().add(SortDir.ASC);
            joiner.add(rph.getSortByList().get(i).getColumnName() + " " + rph.getSortDirList().get(i).name());
        }
        return ORDER_BY_QUERY + joiner.toString();
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate certificate) throws DaoWrongIdException {
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

    private static class ListGiftCertificateExtractor implements ResultSetExtractor<List<GiftCertificate>> {

        @Override
        public List<GiftCertificate> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<String, GiftCertificate> map = new LinkedHashMap<>();
            while (rs.next()) {
                GiftCertificate cert = map.get(rs.getString(CERTIFICATE_ID_COLUMN_NAME));
                if (cert == null) {
                    cert = new GiftCertificate();
                    cert.setId(rs.getLong(CERTIFICATE_ID_COLUMN_NAME));
                    cert.setName(rs.getString(CERTIFICATE_NAME_COLUMN_NAME));
                    cert.setDescription(rs.getString(CERTIFICATE_DESCRIPTION_COLUMN_NAME));
                    cert.setPrice(rs.getInt(CERTIFICATE_PRICE_COLUMN_NAME));
                    cert.setDuration(rs.getInt(CERTIFICATE_DURATION_COLUMN_NAME));
                    cert.setCreateDate(rs.getObject(CERTIFICATE_CREATE_DATE_COLUMN_NAME, LocalDateTime.class));
                    cert.setLastUpdateDate(rs.getObject(CERTIFICATE_LAST_UPDATE_DATE_COLUMN_NAME, LocalDateTime.class));
                    map.put(rs.getString(CERTIFICATE_ID_COLUMN_NAME), cert);
                }
                if (rs.getString(TAG_NAME_COLUMN_NAME) != null) {
                    Tag tag = new Tag();
                    tag.setId(rs.getLong(TAG_ID_COLUMN_NAME));
                    tag.setName(rs.getString(TAG_NAME_COLUMN_NAME));
                    cert.addTag(tag);
                }
            }
            return new LinkedList<>(map.values());
        }
    }
}
