package vn.khanguyen.backend.domain.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.khanguyen.backend.util.constant.GenderEnum;

@Getter
@Setter
public class ReqCreateUserDTO {
    private String name;

    @NotBlank(message = "Email khong duoc de trong")
    private String email;

    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    private int age;
    private GenderEnum gender;
    private String address;

}
