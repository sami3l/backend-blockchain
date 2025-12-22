package com.clinchain.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lot_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    @JsonIgnore
    private Lot lot;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false, length = 50)
    private String actor;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // For JSON serialization - frontend expects "at" field
    @JsonProperty("at")
    @Transient
    public LocalDateTime getAt() {
        return timestamp;
    }
}
