package com.wilzwert.myjobs.infrastructure.security.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Custom UserDetails basic implementation
 * As of now, we do not handle authorities, expiration, activation/deactivation, account lock
 * @author Wilhelm Zwertvaegher
 */
@Builder
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
  @Serial
  private static final long serialVersionUID = 1L;

  private UUID id;

  private String email;

  private String username;

  private String role;

  private String password;

  private Collection<? extends GrantedAuthority> authorities;

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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  } 
}
