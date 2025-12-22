package com.clinchain.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lot {
    @Id
    private String id;

    @Column(name = "med_name", nullable = false)
    private String medName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean validated = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LotStatus status = LotStatus.CREE_PAR_GROSSISTE;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    @Builder.Default
    private List<LotHistory> history = new ArrayList<>();

    public void addHistory(LotHistory historyEntry) {
        history.add(historyEntry);
        historyEntry.setLot(this);
    }
}
