package vn.khanguyen.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.req.auth.ReqCreateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResCreateUserDTO;
import vn.khanguyen.backend.mapper.UserMapper;
import vn.khanguyen.backend.repository.AuthRepository;
import vn.khanguyen.backend.repository.RoleRepository;
import vn.khanguyen.backend.util.constant.RoleEnum;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(AuthRepository authRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResCreateUserDTO createUser(ReqCreateUserDTO reqCreateUserDTO) {
        return createAccount(reqCreateUserDTO, RoleEnum.USER);
    }

    public ResCreateUserDTO createHr(ReqCreateUserDTO reqCreateUserDTO) {
        return createAccount(reqCreateUserDTO, RoleEnum.HR);
    }

    public boolean isEmailExist(String email) {
        return this.authRepository.findByEmail(email) != null;
    }

    private ResCreateUserDTO createAccount(ReqCreateUserDTO reqCreateUserDTO, RoleEnum roleEnum) {
        User user = this.userMapper.toUser(reqCreateUserDTO);
        Role role = this.roleRepository.findByName(roleEnum.toString());

        user.setRole(role);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        User savedUser = this.authRepository.save(user);
        return this.userMapper.toResCreateUserDTO(savedUser);
    }
}
