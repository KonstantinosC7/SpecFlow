package com.myy803.requirements.service;

import com.myy803.requirements.model.User;

/**
 * Service interface for user-related operations.
 * US1: saveUser, isUserPresent
 * US2: updateUser, findByUsername
 * US3: logout is handled by Spring Security — no service method needed
 */


public interface UserService {
    void saveUser(User user);
    boolean isUserPresent(User user);
    void updateUser(User user);
    User findByUsername(String username);
}
