package com.clinchain.backend.repository;

import com.clinchain.backend.model.Lot;
import com.clinchain.backend.model.LotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, String> {
    List<Lot> findByCreatedBy(String createdBy);

    List<Lot> findAllByOrderByCreatedAtDesc();

    Page<Lot> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Lot> findByStatus(LotStatus status);

    Page<Lot> findByStatus(LotStatus status, Pageable pageable);

    Page<Lot> findByCreatedBy(String createdBy, Pageable pageable);

    @Query("SELECT l FROM Lot l WHERE " +
            "(:status IS NULL OR l.status = :status) AND " +
            "(:createdBy IS NULL OR l.createdBy = :createdBy) AND " +
            "(:medName IS NULL OR LOWER(l.medName) LIKE LOWER(CONCAT('%', :medName, '%'))) " +
            "ORDER BY l.createdAt DESC")
    Page<Lot> findWithFilters(@Param("status") LotStatus status,
            @Param("createdBy") String createdBy,
            @Param("medName") String medName,
            Pageable pageable);

    long countByStatus(LotStatus status);

    @Query("SELECT SUM(l.quantity) FROM Lot l")
    Integer sumTotalQuantity();
}
