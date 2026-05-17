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

import jakarta.validation.Valid;
import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.domain.dto.ResultPaginationDTO;
import vn.khanguyen.backend.service.RoleService;
import vn.khanguyen.backend.util.annotation.ApiMessage;
import vn.khanguyen.backend.util.error.ResourceNotFoundException;

@RestController
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody @Valid Role role) {
        Role createdRole = this.roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody @Valid Role role) throws ResourceNotFoundException {
        Role updatedRole = this.roleService.updateRole(role);
        if (updatedRole == null) {
            throw new ResourceNotFoundException("Role with id " + role.getId() + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedRole);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws ResourceNotFoundException {
        Role role = this.roleService.getRoleById(id);
        if (role == null) {
            throw new ResourceNotFoundException("Role with id " + id + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }


    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws ResourceNotFoundException {
        Role deletedRole = this.roleService.deleteRole(id);
        if (deletedRole == null) {
            throw new ResourceNotFoundException("Role with id " + id + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
