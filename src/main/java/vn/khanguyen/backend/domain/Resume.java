package vn.khanguyen.backend.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.khanguyen.backend.util.SecurityUtil;
import vn.khanguyen.backend.util.constant.ResumeStateEnum;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "email",
        "url",
        "status",
        "createdAt",
        "updatedAt",
        "createdBy",
        "updatedBy",
})
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String url;

    private ResumeStateEnum status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;


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