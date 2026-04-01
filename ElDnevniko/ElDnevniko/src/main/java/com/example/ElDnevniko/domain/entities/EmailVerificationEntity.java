    package com.example.ElDnevniko.domain.entities;

    import java.time.LocalDateTime;
    import java.util.UUID;

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

        private LocalDateTime createdAt;

        private boolean verified;

        public EmailVerificationEntity(UserEntity user)
        {
            this.user = user;
            this.token = UUID.randomUUID().toString();
            this.createdAt = LocalDateTime.now();
            this.verified = false;
        }
    }
