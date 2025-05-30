package com.project.airbnb_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {

    private Long id;

    @JsonIgnore
    private User user;

    private String name;

    private Gender gender;

    private Integer age;
}
