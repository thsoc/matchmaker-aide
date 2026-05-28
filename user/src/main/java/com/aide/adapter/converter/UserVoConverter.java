package com.aide.adapter.converter;

/**
 * @author mazg
 * @description 用户VO转换器实现
 * @date 2026/5/28
 * @date 12:25
 */
import com.aide.adapter.VO.LoginResponse;
import com.aide.adapter.VO.RegisterRequest;
import com.aide.domain.model.UserDo;
import org.springframework.stereotype.Component;

@Component
public class UserVoConverter{

    public LoginResponse toLoginResponse(UserDo userDo, String token) {
        if (userDo == null) {
            return null;
        }

        return LoginResponse.builder()
                .userId(userDo.getId())
                .account(userDo.getAccount())
                .username(userDo.getUsername())
                .token(token)
                .role(userDo.getRole())
                .status(userDo.getStatus())
                .build();
    }

    public UserDo fromRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }

        return UserDo.builder()
                .account(registerRequest.getAccount())
                .password(registerRequest.getPassword())
                .username(registerRequest.getUsername())
                .mobile(registerRequest.getMobile())
                .email(registerRequest.getEmail())
                .sex(registerRequest.getSex())
                .birthday(registerRequest.getBirthday())
                .occupation(registerRequest.getOccupation())
                .build();
    }
}

