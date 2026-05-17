package vn.khanguyen.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.dto.LoginDTO;
import vn.khanguyen.backend.domain.req.auth.ReqCreateUserDTO;
import vn.khanguyen.backend.domain.req.auth.RestLoginDTO;
import vn.khanguyen.backend.domain.res.auth.ResAccountDTO;
import vn.khanguyen.backend.domain.res.user.ResCreateUserDTO;
import vn.khanguyen.backend.mapper.UserMapper;
import vn.khanguyen.backend.service.AuthService;
import vn.khanguyen.backend.service.UserService;
import vn.khanguyen.backend.util.SecurityUtil;
import vn.khanguyen.backend.util.annotation.ApiMessage;
import vn.khanguyen.backend.util.error.ResourceNotFoundException;

@RestController
public class AuthController {
    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, AuthService authService, UserMapper userMapper) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login to system")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername // xac thuc password
        // va truyen username, passw
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // lay thong tin user
        RestLoginDTO res = new RestLoginDTO();
        User currentUser = this.userService.getUserByUsername(loginDTO.getUsername());
        if (currentUser != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                    currentUser.getName());
            res.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        res.setAccessToken(access_token);

        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());
        res.setRefreshToken(refresh_token);

        System.out.println(">>>> token:   " + refresh_token);
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);

    }

    @PostMapping("/auth/register")
    @ApiMessage("Register user account")
    public ResponseEntity<ResCreateUserDTO> registerUser(@Valid @RequestBody ReqCreateUserDTO reqCreateUserDTO)
            throws ResourceNotFoundException {
        if (this.authService.isEmailExist(reqCreateUserDTO.getEmail())) {
            throw new ResourceNotFoundException(
                    "Email " + reqCreateUserDTO.getEmail() + " da ton tai, vui long su dung email khac");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.createUser(reqCreateUserDTO));
    }

    @PostMapping("/auth/register/hr")
    @ApiMessage("Register HR account")
    public ResponseEntity<ResCreateUserDTO> registerHr(@Valid @RequestBody ReqCreateUserDTO reqCreateUserDTO)
            throws ResourceNotFoundException {
        if (this.authService.isEmailExist(reqCreateUserDTO.getEmail())) {
            throw new ResourceNotFoundException(
                    "Email " + reqCreateUserDTO.getEmail() + " da ton tai, vui long su dung email khac");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.createHr(reqCreateUserDTO));
    }

    @GetMapping("/auth/account")
    @ApiMessage("Decode token and get account info")
    public ResponseEntity<ResAccountDTO> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.getUserByUsername(email);
        return ResponseEntity.ok().body(this.userMapper.toResAccountDTO(currentUser));

    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user By refresh token")
    public ResponseEntity<RestLoginDTO> getFreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "null") String refresh_token)
            throws ResourceNotFoundException {

        // check cookies
        if (refresh_token.equals("null")) {
            throw new ResourceNotFoundException("Bạn không có refresh token ở cookie");
        }
        // check valid token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new ResourceNotFoundException("Refresh Token không hợp lệ");
        }

        // tao token moi
        RestLoginDTO res = new RestLoginDTO();
        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName());
        res.setUser(userLogin);

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(access_token);

        // create new refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        // update cookie (luu vao cookie)
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);

    }

    @PostMapping("/auth/logout")
    @ApiMessage("User logout")
    public ResponseEntity<Void> logOut()
            throws ResourceNotFoundException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email == null) {
            throw new ResourceNotFoundException("Accees token không hợp lệ");
        }
        this.userService.updateUserToken(null, email);

        ResponseCookie deleteSpringCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);

    }


}
