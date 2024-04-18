package com.crud.auth.model.enums;

public enum Role {

    ADMIN("admin"),
    USER("user");

    private String role;

    Role(String role) {
        this.role = role;
    }
}
