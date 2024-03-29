package com.example.asp_library.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN, TEACHER, STUDENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
