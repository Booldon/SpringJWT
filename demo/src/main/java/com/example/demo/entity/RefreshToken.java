package com.example.demo.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import com.example.demo.util.SecureRandomStringGenerator;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @Column(unique = true, nullable = false)
    private String username;

    private String randomKey;

    private Date limitTime;

    private String role;

    public void setLimitTime() {
        this.limitTime = new Date(System.currentTimeMillis() + 60*60*1000*24);
    }
}
