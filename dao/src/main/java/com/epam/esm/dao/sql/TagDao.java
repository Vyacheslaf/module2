package com.epam.esm.dao.sql;

import com.epam.esm.dao.Dao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoUnsupportedOperationException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequestScope
public class TagDao implements Dao<Tag> {
    private JdbcTemplate jdbcTemplate;
    private static final String CREATE_QUERY = "INSERT INTO tag (name) VALUES (?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM tag WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM tag ORDER BY name";
    private static final String DELETE_QUERY = "DELETE FROM tag WHERE id = ?";
    private static final String RESOURCE_NAME = "Tag";

    @Autowired
    public TagDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Tag create(Tag tag) throws DaoDuplicateKeyException {
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
    public Tag findById(long id) throws DaoWrongIdException {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new BeanPropertyRowMapper<>(Tag.class), id)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongIdException(id, RESOURCE_NAME);
        }
    }

    @Override
    public List<Tag> findAll(RequestParametersHolder rph) {
        return jdbcTemplate.query(FIND_ALL_QUERY, new BeanPropertyRowMapper<>(Tag.class));
    }

    @Override
    public Tag update(Tag tag) throws DaoException {
        throw new DaoUnsupportedOperationException();
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_QUERY, id);
    }
}
