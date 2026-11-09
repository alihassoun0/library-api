package com.example.libraryapi.repository;

import com.example.libraryapi.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
