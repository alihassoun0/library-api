package com.example.libraryapi.repository;

import com.example.libraryapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByBookIdAndMemberIdAndActiveTrue(Long bookId, Long memberId);

    List<Reservation> findByMemberIdAndActiveTrue(Long memberId);

    Optional<Reservation> findByIdAndMemberId(Long id, Long memberId);
}
