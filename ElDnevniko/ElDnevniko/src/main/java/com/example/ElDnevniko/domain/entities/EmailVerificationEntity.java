    package com.example.ElDnevniko.domain.entities;

    import java.security.SecureRandom;
    import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.OneToOne;
    import jakarta.persistence.Table;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Entity
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "email_verifications")
    public class EmailVerificationEntity {
        @Id
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        private int id;

        @OneToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntity user;

        private String token;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        private boolean verified;

        public EmailVerificationEntity(UserEntity user)
        {
            this.user = user;
            SecureRandom random = new SecureRandom();
            int code = 100000 + random.nextInt(900000);
            this.token = String.valueOf(code);
            this.createdAt = LocalDateTime.now();
            this.verified = false;
        }
    }
