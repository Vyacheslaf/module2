package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class UserServiceImpl extends AbstractService<User> {
    @Autowired
    public UserServiceImpl(Dao<User> dao) {
        super(dao);
    }
}
