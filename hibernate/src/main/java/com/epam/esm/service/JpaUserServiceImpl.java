package com.epam.esm.service;

import com.epam.esm.model.User;
import com.epam.esm.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaUserServiceImpl extends JpaAbstractService<User> {

    @Autowired
    public JpaUserServiceImpl(Repository<User> userRepository) {
        super(userRepository);
    }
}
