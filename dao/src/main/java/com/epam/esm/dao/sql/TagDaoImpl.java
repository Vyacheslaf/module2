package com.epam.esm.dao.sql;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.*;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class TagDaoImpl extends AbstractDao<Tag> implements TagDao {
    private static final String CREATE_QUERY = "INSERT INTO tag (name) VALUES (?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM tag WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM tag ORDER BY name LIMIT ? OFFSET ?";
    private static final String DELETE_QUERY = "DELETE FROM tag WHERE id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT t.id, t.name  FROM " +
                            "(SELECT gift_certificate_id, cost FROM `order` WHERE user_id = ?) AS o " +
                            "LEFT JOIN gift_certificate_tag gct ON o.gift_certificate_id = gct.gift_certificate_id " +
                            "LEFT JOIN tag t ON gct.tag_id = t.id " +
                            "GROUP BY gct.tag_id " +
                            "ORDER BY SUM(cost) DESC " +
                            "LIMIT 1";
    private static final String CHECK_IF_CERTIFICATE_EXIST_QUERY = "SELECT COUNT(1) FROM gift_certificate WHERE id = ?";
    private static final String RESOURCE_NAME = "Tag";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Tag create(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, tag.getName());
                return pstmt;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DaoDuplicateKeyException(e, tag.getName());
        }
        tag.setId(keyHolder.getKey().longValue());
        return tag;
    }

    @Override
    public Tag findById(long id) {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new BeanPropertyRowMapper<>(Tag.class), id)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongIdException(id, RESOURCE_NAME);
        }
    }

    @Override
    public List<Tag> findAll(RequestParametersHolder rph) {
        return jdbcTemplate.query(FIND_ALL_QUERY, new BeanPropertyRowMapper<>(Tag.class),
                                  rph.getSize(), rph.getOffset());
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_QUERY, id);
    }

    @Override
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        try {
            return jdbcTemplate.query(FIND_BY_USER_ID_QUERY, new BeanPropertyRowMapper<>(Tag.class), userId)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoTagForUserNotFoundException(userId);
        }
    }

    @Override
    @Transactional
    public List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph) {
        if (jdbcTemplate.queryForObject(CHECK_IF_CERTIFICATE_EXIST_QUERY, Long.class, giftCertificateId) == 0) {
            throw new DaoWrongIdException(giftCertificateId, "GiftCertificate");
        }
        String FIND_GIFT_CERTIFICATE_TAGS_QUERY = "select * from tag where id in (select tag_id from gift_certificate_tag where gift_certificate_id = ?) limit ? offset ?";
        return jdbcTemplate.query(FIND_GIFT_CERTIFICATE_TAGS_QUERY, new BeanPropertyRowMapper<>(Tag.class),
                                  giftCertificateId, rph.getSize(), rph.getOffset());
    }
}
