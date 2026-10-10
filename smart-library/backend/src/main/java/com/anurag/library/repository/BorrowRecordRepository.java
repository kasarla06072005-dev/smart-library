package com.anurag.library.repository;

import com.anurag.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByStudentIdOrderByBorrowedAtDesc(Long studentId);
    List<BorrowRecord> findAllByOrderByBorrowedAtDesc();
    long countByBookIdAndReturnedFalse(Long bookId);
}
