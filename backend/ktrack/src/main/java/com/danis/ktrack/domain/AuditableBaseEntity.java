package com.danis.ktrack.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * A base class for entities that need auditing (created/modified by and at).
 *
 * Entities that extend this class will automatically have these fields
 * populated by Spring Data JPA.
 */
@Data
@MappedSuperclass // Tells JPA this is not an entity itself, but its fields should be in its children.
@EntityListeners(AuditingEntityListener.class) // This is what triggers the auditing.
public abstract class AuditableBaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at")
    protected LocalDateTime lastModifiedAt;

    // We use String to store the username.
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    protected String lastModifiedBy;
}
