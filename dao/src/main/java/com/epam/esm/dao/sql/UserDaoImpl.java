package com.epam.esm.dao.sql;

import com.epam.esm.entity.User;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.util.*;

@Repository
@RequestScope
public class UserDaoImpl extends AbstractDao<User> {
    private static final String FIND_ALL_QUERY = "SELECT id, username FROM `user` LIMIT ? OFFSET ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM `user` WHERE id = ?";
    private static final String RESOURCE_NAME = "User";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<User> findAll(RequestParametersHolder rph) throws DaoException {
        return jdbcTemplate.query(FIND_ALL_QUERY, new BeanPropertyRowMapper<>(User.class),
                                  rph.getSize(), getOffset(rph));
    }


    @Override
    public User findById(long id) throws DaoException {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new BeanPropertyRowMapper<>(User.class), id)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongIdException(e, id, RESOURCE_NAME);
        }
    }
}
