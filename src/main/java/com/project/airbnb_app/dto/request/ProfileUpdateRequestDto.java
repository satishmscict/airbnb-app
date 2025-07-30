package com.project.airbnb_app.dto.request;

import com.project.airbnb_app.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {
    private LocalDate dateOfBirth;
    private Gender gender;
    private String name;
}
