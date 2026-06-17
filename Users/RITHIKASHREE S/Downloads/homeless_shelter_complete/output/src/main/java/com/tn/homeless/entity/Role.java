package com.tn.homeless.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a user role in the system (ADMIN, NGO, VOLUNTEER).
 *
 * CHANGES from original:
 *  - Removed redundant Lombok annotations (@Data, @AllArgsConstructor, @NoArgsConstructor)
 *    because manual getters/setters were already written. Keeping both causes
 *    Lombok to generate duplicate methods and causes compile errors.
 *  - Added @NotBlank validation on name.
 *  - Added explicit equals/hashCode based on 'name' so Role can safely be used
 *    in HashSet inside User (important for JPA detached-entity merging).
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name cannot be blank")
    @Column(unique = true, nullable = false)
    private String name; // Allowed values: ADMIN, NGO, VOLUNTEER

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name != null && name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
