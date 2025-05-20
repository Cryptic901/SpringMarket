package by.cryptic.springmarket.model;

import by.cryptic.springmarket.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", schema = "public")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    private Boolean enabled = false;

    private Character gender;

    @Column(name = "verify_code")
    private Integer verifyCode;

    @Column(name = "verification_expires")
    private OffsetDateTime verificationCodeExpiresAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Cart cart;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @ToString.Exclude
    private List<CustomerOrder> orders = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy")
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();

    public AppUser(String username, String email, String password, Role role, String phoneNumber, Character gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(id, appUser.id) &&
                Objects.equals(username, appUser.username) &&
                Objects.equals(password, appUser.password) &&
                Objects.equals(email, appUser.email) &&
                Objects.equals(phoneNumber, appUser.phoneNumber) &&
                role == appUser.role &&
                Objects.equals(enabled, appUser.enabled) &&
                Objects.equals(gender, appUser.gender) &&
                Objects.equals(verifyCode, appUser.verifyCode) &&
                Objects.equals(createdAt, appUser.createdAt) &&
                Objects.equals(cart, appUser.cart) &&
                Objects.equals(orders, appUser.orders) &&
                Objects.equals(reviews, appUser.reviews);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, phoneNumber, role, enabled, gender
                , verifyCode, createdAt, cart, orders, reviews);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
