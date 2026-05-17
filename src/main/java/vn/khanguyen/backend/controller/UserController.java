package vn.khanguyen.backend.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.dto.ResultPaginationDTO;
import vn.khanguyen.backend.domain.res.user.ResCreateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResUpdateUserDTO;
import vn.khanguyen.backend.domain.res.user.ResUserDTO;
import vn.khanguyen.backend.service.UserService;
import vn.khanguyen.backend.util.annotation.ApiMessage;
import vn.khanguyen.backend.util.error.ResourceNotFoundException;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody User user) throws ResourceNotFoundException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new ResourceNotFoundException(
                    "Email" + user.getEmail() + "đã tồn tại, vui lòng sử dụng email khác");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(user));

    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws ResourceNotFoundException {
        User updatedUser = this.userService.updateUser(user);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("User with id " + user.getId() + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToUpdateUser(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (this.userService.deleteUser(id) == null) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws ResourceNotFoundException {
        User user = this.userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToUser(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(spec, pageable));
    }
}
