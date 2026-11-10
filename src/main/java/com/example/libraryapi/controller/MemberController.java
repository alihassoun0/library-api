package com.example.libraryapi.controller;

import com.example.libraryapi.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.libraryapi.mapper.MemberMapper;
import com.example.libraryapi.service.MemberService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
@Tag(name = "Members", description = "Manage library members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @GetMapping
    @Operation(summary = "List all members")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<MemberDTO> getAllMembers() {
        return memberService.getAllMembers().stream().map(memberMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a member by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public MemberDTO getMemberById(@PathVariable Long id) {
        return memberMapper.toDTO(memberService.getMemberById(id));
    }

    @PostMapping
    @Operation(summary = "Create a member")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO member) {
        MemberDTO saved = memberMapper.toDTO(memberService.createMember(memberMapper.toEntity(member)));
        return ResponseEntity.created(URI.create("/members/" + saved.id())).body(saved);
    }
}
