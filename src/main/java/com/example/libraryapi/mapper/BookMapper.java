package com.example.libraryapi.mapper;

import com.example.libraryapi.dto.BookDTO;
import com.example.libraryapi.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDTO(Book entity) {
        if (entity == null) {
            return null;
        }
        return new BookDTO(entity.getId(), entity.getIsbn(), entity.getTitle(), entity.getAuthor(), entity.getGenre(), entity.getPublishedYear(), entity.isAvailable());
    }

    public Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        Book entity = new Book();
        entity.setId(dto.id());
        entity.setIsbn(dto.isbn());
        entity.setTitle(dto.title());
        entity.setAuthor(dto.author());
        entity.setGenre(dto.genre());
        entity.setPublishedYear(dto.publishedYear());
        entity.setAvailable(dto.available());
        return entity;
    }
}
