package vn.khanguyen.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.req.auth.ReqCreateUserDTO;
import vn.khanguyen.backend.domain.res.auth.ResAccountDTO;
import vn.khanguyen.backend.domain.res.user.ResCreateUserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ResCreateUserDTO toResCreateUserDTO(User user);

    ResAccountDTO toResAccountDTO(User user);

    ResAccountDTO.RoleAccount toRoleAccount(Role role);

    User toUser(ResCreateUserDTO resCreateUserDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "company", ignore = true)
    User toUser(ReqCreateUserDTO reqCreateUserDTO);

}
