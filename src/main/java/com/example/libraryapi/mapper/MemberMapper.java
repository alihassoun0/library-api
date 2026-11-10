package com.example.libraryapi.mapper;

import com.example.libraryapi.dto.MemberDTO;
import com.example.libraryapi.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberDTO toDTO(Member entity) {
        if (entity == null) {
            return null;
        }
        return new MemberDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getMembershipDate(), entity.isActive());
    }

    public Member toEntity(MemberDTO dto) {
        if (dto == null) {
            return null;
        }
        Member entity = new Member();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setMembershipDate(dto.membershipDate());
        entity.setActive(dto.active());
        return entity;
    }
}
