package com.example.libraryapi.controller;

import com.example.libraryapi.entity.Member;
import com.example.libraryapi.repository.MemberRepository;
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

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        member.setId(null);
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.created(URI.create("/members/" + savedMember.getId()))
                .body(savedMember);
    }
}
