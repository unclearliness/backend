package vn.khanguyen.backend.domain.res.user;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.khanguyen.backend.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;

    private String name;
    private String email;

    private int age;

    private GenderEnum gender;

    private String address;

    private CompanyUser company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }

}