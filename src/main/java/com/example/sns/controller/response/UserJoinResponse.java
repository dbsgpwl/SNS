package com.example.sns.controller.response;

import com.example.sns.model.User;
import com.example.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class UserJoinResponse
{
    private String userName;
    private Integer id;
    private UserRole userRole;

    public static  UserJoinResponse fromUser(User user){
        return new UserJoinResponse(
                user.getUsername(),
                user.getId(),
                user.getUserRole()
                );
    }
}
