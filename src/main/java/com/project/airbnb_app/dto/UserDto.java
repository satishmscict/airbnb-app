package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private Set<Role> roles;
}
