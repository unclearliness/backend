package vn.khanguyen.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khanguyen.backend.domain.Role;
import vn.khanguyen.backend.domain.dto.Meta;
import vn.khanguyen.backend.domain.dto.ResultPaginationDTO;
import vn.khanguyen.backend.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Role existingRole = this.roleRepository.findById(role.getId()).orElse(null);
        if (existingRole != null) {
            existingRole.setName(role.getName());
            existingRole.setDescription(role.getDescription());
            existingRole.setActive(role.isActive());
            existingRole.setPermissions(role.getPermissions());
            return this.roleRepository.save(existingRole);
        }
        return null;
    }

    public Role getRoleById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }


    public Role deleteRole(long id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        if (role != null) {
            this.roleRepository.delete(role);
            return role;
        }
        return null;
    }
}
