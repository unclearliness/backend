// vn/khanguyen/backend/domain/User.java

package vn.khanguyen.backend.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.khanguyen.backend.util.SecurityUtil;
import vn.khanguyen.backend.util.constant.GenderEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "name",
        "email",
        "password",
        "age",
        "gender",
        "address",
        "refreshToken",
        "createdAt",
        "updatedAt",
        "createdBy",
        "updatedBy",
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Resume> resumes;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleAfterUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

    // constructors, getters, and setters
}