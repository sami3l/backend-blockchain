package com.clinchain.backend.repository;

import com.clinchain.backend.model.LotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotHistoryRepository extends JpaRepository<LotHistory, Long> {
}
