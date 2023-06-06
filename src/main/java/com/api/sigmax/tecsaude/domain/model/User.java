package com.api.sigmax.tecsaude.domain.model;

import com.api.sigmax.tecsaude.domain.dtos.RegisterUserDto;
import com.api.sigmax.tecsaude.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_USER")
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true)
    private String username;
    private String password;
    private String cpf;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(RegisterUserDto dto){
        this.username = dto.username();
        this.password = dto.password();
        this.cpf = dto.cpf();
        this.role = UserRole.PATIENT;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
