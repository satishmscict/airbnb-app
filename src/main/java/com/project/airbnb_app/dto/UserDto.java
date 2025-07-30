package com.project.airbnb_app.dto;

import com.project.airbnb_app.entity.enums.Gender;
import com.project.airbnb_app.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private Gender gender;

    private LocalDate dateOfBirth;

    private Set<Role> roles;
}
