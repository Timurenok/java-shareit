package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserDto {
    private String name;
    @Email
    private String email;

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }
}
