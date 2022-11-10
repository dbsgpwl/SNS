package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.UserEntityFixture;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;
    @Test
    void join_success(){
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName,password,1);


        // moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName,password,1));


        Assertions.assertDoesNotThrow(() -> userService.join(userName,password));
    }

    @Test
    void join_same_error(){
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName,password,1);

        // moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());

        //Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName,password));
    }

    @Test
    void login_success(){
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName,password,1);

        // moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        //when(encoder.matches(password, fixture.getPassword())).thenReturn(true);
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName,password));
    }


    @Test
    void login_noting_userName_error(){
        String userName = "userName";
        String password = "password";


        // moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());


        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());

        //Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName,password));
    }

    @Test
    void login_password_fail_error(){
        String userName = "userName";
        String password = "password";
        String wrongPw = "wrongPw";
        UserEntity fixture = UserEntityFixture.get(userName,password,1);


        // moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());

        //Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName,wrongPw));
    }
}
