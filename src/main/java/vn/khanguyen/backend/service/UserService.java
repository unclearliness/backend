package vn.khanguyen.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.khanguyen.backend.domain.Company;
import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.dto.Meta;
import vn.khanguyen.backend.domain.dto.ResultPaginationDTO;
import vn.khanguyen.backend.domain.req.auth.ReqCreateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResCreateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResUpdateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResUserDTO;
import vn.khanguyen.backend.mapper.UserMapper;
import vn.khanguyen.backend.repository.RoleRepository;
import vn.khanguyen.backend.repository.UserRepository;
import vn.khanguyen.backend.util.constant.RoleEnum;
import vn.khanguyen.backend.util.error.ResourceNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public ResCreateUserDTO createUser(User user) {
        Role role = this.roleRepository.findByName(RoleEnum.USER.toString());
        // set role
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // check company
        if (user.getCompany() != null) {
            Company companyOpt = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOpt);
        }
        User savedUser = userRepository.save(user);
        return userMapper.toResCreateUserDTO(savedUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageUser.getNumber() + 1); // 1-based for response
        mt.setPageSize(pageUser.getSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        List<ResUserDTO> listUsers = pageUser.getContent().stream().map(this::convertToUser).toList();
        rs.setResult(listUsers);

        return rs;
    }

    public User updateUser(User userCur) {
        User user = userRepository.findById(userCur.getId()).orElse(null);
        if (user != null) {
            user.setAddress(userCur.getAddress());
            user.setGender(userCur.getGender());
            user.setAge(userCur.getAge());
            user.setName(userCur.getName());

            // check company
            if (userCur.getCompany() != null) {
                Company company = this.companyService.findById(userCur.getCompany().getId());
                user.setCompany(company);
            }
            return userRepository.save(user);
        }
        return user;
    }

    public User deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            return user;
        }
        return null;
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.findByEmail(email) != null;
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User user = this.getUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public ResCreateUserDTO convertToCreateUser(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        // set company
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
        }
        res.setCompany(com);
        return res;
    }

    public ResUpdateUserDTO convertToUpdateUser(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setUpdatedBy(user.getUpdatedBy());

        // set company
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
        }
        res.setCompany(com);
        return res;
    }

    public ResUserDTO convertToUser(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        // set company
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
        }
        res.setCompany(com);
        return res;
    }

    // public RestLoginDTO convertToRestLogin(User user, String accessToken) {
    // RestLoginDTO res = new RestLoginDTO();
    // RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(user.getId(),
    // user.getEmail(),
    // user.getName());
    // res.setUser(userLogin);
    // res.setAccessToken(accessToken);
    // return res;
    // }
}
