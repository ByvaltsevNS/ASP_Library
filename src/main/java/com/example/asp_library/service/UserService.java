package com.example.asp_library.service;

import com.example.asp_library.domain.Role;
import com.example.asp_library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import com.example.asp_library.domain.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        User user = null;

        if (userRepository.findById(id).isPresent()) {
            user = userRepository.findById(id).get();
        }

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean createUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return true;
    }

    public boolean updateUser(User user) {
        User anotherUser = this.getUserByUsername(user.getUsername());

        if (anotherUser == null || user.equals(anotherUser)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void changePassword(String oldPassword, String newPassword) {

    }

    public boolean userExists(String username) {
        return false;
    }
}
