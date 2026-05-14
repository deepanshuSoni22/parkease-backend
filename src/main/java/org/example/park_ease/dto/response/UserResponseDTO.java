package org.example.park_ease.dto.response;

import org.example.park_ease.enums.Role;

public class UserResponseDTO {

    private String username;
    private Role role;

    public UserResponseDTO() {
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
