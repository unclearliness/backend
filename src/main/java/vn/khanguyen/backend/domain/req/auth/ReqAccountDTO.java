package vn.khanguyen.backend.domain.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.util.constant.GenderEnum;

@Getter
@Setter
public class ReqAccountDTO {
    private long id;

    private String name;

    private String email;

    private Role role;

}