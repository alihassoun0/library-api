package com.example.libraryapi.mapper;

import com.example.libraryapi.dto.ReservationDTO;
import com.example.libraryapi.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final BookMapper bookMapper;
    private final MemberMapper memberMapper;

    public ReservationMapper(BookMapper bookMapper, MemberMapper memberMapper) {
        this.bookMapper = bookMapper;
        this.memberMapper = memberMapper;
    }

    public ReservationDTO toDTO(Reservation entity) {
        if (entity == null) {
            return null;
        }
        return new ReservationDTO(entity.getId(), bookMapper.toDTO(entity.getBook()), memberMapper.toDTO(entity.getMember()), entity.getReservationDate(), entity.isActive());
    }

    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation entity = new Reservation();
        entity.setId(dto.id());
        entity.setBook(bookMapper.toEntity(dto.book()));
        entity.setMember(memberMapper.toEntity(dto.member()));
        entity.setReservationDate(dto.reservationDate());
        entity.setActive(dto.active());
        return entity;
    }
}
