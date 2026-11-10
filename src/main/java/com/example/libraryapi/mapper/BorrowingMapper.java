package com.example.libraryapi.mapper;

import com.example.libraryapi.dto.BorrowingDTO;
import com.example.libraryapi.entity.Borrowing;
import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {

    private final BookMapper bookMapper;
    private final MemberMapper memberMapper;

    public BorrowingMapper(BookMapper bookMapper, MemberMapper memberMapper) {
        this.bookMapper = bookMapper;
        this.memberMapper = memberMapper;
    }

    public BorrowingDTO toDTO(Borrowing entity) {
        if (entity == null) {
            return null;
        }
        return new BorrowingDTO(entity.getId(), bookMapper.toDTO(entity.getBook()), memberMapper.toDTO(entity.getMember()), entity.getBorrowDate(), entity.getDueDate(), entity.getReturnDate(), entity.getFineAmount());
    }

    public Borrowing toEntity(BorrowingDTO dto) {
        if (dto == null) {
            return null;
        }
        Borrowing entity = new Borrowing();
        entity.setId(dto.id());
        entity.setBook(bookMapper.toEntity(dto.book()));
        entity.setMember(memberMapper.toEntity(dto.member()));
        entity.setBorrowDate(dto.borrowDate());
        entity.setDueDate(dto.dueDate());
        entity.setReturnDate(dto.returnDate());
        entity.setFineAmount(dto.fineAmount());
        return entity;
    }
}
