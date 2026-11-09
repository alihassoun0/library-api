package com.example.libraryapi.repository;

import com.example.libraryapi.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
}
