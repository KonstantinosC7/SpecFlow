package com.myy803.requirements.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.model.Role;
import com.myy803.requirements.model.User;

/**
 * Implements both our UserService and Spring Security's UserDetailsService.
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userMapper.save(user);
    }

    @Override
    public boolean isUserPresent(User user) {
        Optional<User> storedUser = userMapper.findByUsername(user.getUsername());
        return storedUser.isPresent();
    }

    @Override
    public void updateUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
    }

    /** Called by Spring Security on every login attempt */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "USER_NOT_FOUND: " + username));
    }
}
