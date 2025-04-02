package com.tp.opencourse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "dob")
    private LocalDate dob;
    @Column(name = "sex")
    private boolean sex;
    @Column(name = "avt")
    private String avt;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name="user_role",
        joinColumns = {@JoinColumn(name="user_id")},
        inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    List<Role> roles;

    @OneToMany(mappedBy = "student")
    private List<Register> registers;
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
