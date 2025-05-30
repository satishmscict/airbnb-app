package com.project.airbnb_app.config;

import com.project.airbnb_app.dto.UserDto;
import com.project.airbnb_app.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // When a booking is completed, converting the Booking entity to BookingDto fails.
        // To resolve this issue, the following configuration was added.
        Converter<User, UserDto> userToUserDtoConverter = ctx -> {
            User source = ctx.getSource();
            if (source == null) return null;
            return new UserDto(source.getId(), source.getName(), source.getEmail(), source.getPassword(), source.getRoles());
        };
        modelMapper.typeMap(User.class, UserDto.class).setConverter(userToUserDtoConverter);

        return modelMapper;
    }
}
