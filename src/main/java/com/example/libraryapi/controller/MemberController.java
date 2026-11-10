package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Borrowing;
import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.service.BorrowingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final BorrowingService borrowingService;

    public MemberController(MemberRepository memberRepository, BorrowingService borrowingService) {
        this.memberRepository = memberRepository;
        this.borrowingService = borrowingService;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    @GetMapping("/{id}/borrowings")
    public List<Borrowing> getActiveBorrowings(@PathVariable Long id) {
        return borrowingService.getActiveBorrowings(id);
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        member.setId(null);
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.created(URI.create("/members/" + savedMember.getId()))
                .body(savedMember);
    }
}
