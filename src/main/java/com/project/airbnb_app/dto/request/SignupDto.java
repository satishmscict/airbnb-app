package com.project.airbnb_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignupDto {
    @NotEmpty(message = "Please enter name.")
    private String name;

    @Email(message = "Please enter valid email.")
    private String email;

    @NotEmpty(message = "Please enter password.")
    private String password;
}
