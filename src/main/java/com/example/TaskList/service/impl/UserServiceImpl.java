package com.example.TaskList.service.impl;

import com.example.TaskList.service.UserService;
import com.example.TaskList.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getById(Long id) {
        return null;
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public boolean isTaskOwner(Long userid, Long taskld) {
        return false;
    }

    @Override
    public void delete(Long id) {

    }
}
