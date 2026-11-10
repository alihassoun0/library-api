package com.example.libraryapi.controller;

import com.example.libraryapi.dto.*;
import com.example.libraryapi.mapper.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiDesignContractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BorrowingMapper borrowingMapper;
    @Autowired
    private ReservationMapper reservationMapper;

    @Test
    void bookDto_shouldPreserveCrudJsonStatusesAndLocation() throws Exception {
        String body = "{\"id\":-1,\"isbn\":\"test-isbn\",\"title\":\"DTO Book\",\"author\":\"Author\","
                + "\"genre\":\"Engineering\",\"publishedYear\":2020,\"available\":true}";
        String created = mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("DTO Book"))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").longValue();
        assertNotEquals(-1, id);
        mockMvc.perform(get("/books/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("test-isbn"));
        mockMvc.perform(put("/books/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("DTO Book", "Updated DTO Book")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated DTO Book"));
        mockMvc.perform(delete("/books/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/books/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void memberDto_shouldPreserveCreationAndReadContract() throws Exception {
        String response = mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":-1,\"name\":\"DTO Member\",\"email\":\"dto@example.com\","
                                + "\"membershipDate\":\"2026-11-10\",\"active\":true}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").longValue();
        assertNotEquals(-1, id);
        mockMvc.perform(get("/members/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("DTO Member"))
                .andExpect(jsonPath("$.membershipDate").value("2026-11-10"));
    }

    @Test
    void mappers_shouldRoundTripAllFieldsIncludingNestedRelationships() {
        BookDTO book = new BookDTO(1L, "isbn", "Title", "Author", "Genre", 2020, false);
        MemberDTO member = new MemberDTO(2L, "Name", "email@example.com", LocalDate.of(2026, 11, 1), true);
        BorrowingDTO borrowing = new BorrowingDTO(3L, book, member, LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 16), new BigDecimal("0.50"));
        ReservationDTO reservation = new ReservationDTO(4L, book, member, LocalDate.of(2026, 11, 2), true);
        assertEquals(book, bookMapper.toDTO(bookMapper.toEntity(book)));
        assertEquals(member, memberMapper.toDTO(memberMapper.toEntity(member)));
        assertEquals(borrowing, borrowingMapper.toDTO(borrowingMapper.toEntity(borrowing)));
        assertEquals(reservation, reservationMapper.toDTO(reservationMapper.toEntity(reservation)));
    }

    @Test
    void controllersAndDtoFields_shouldHaveNoJpaEntityTypes() {
        for (Class<?> controller : List.of(BookController.class, MemberController.class,
                BorrowingController.class, ReservationController.class)) {
            for (var method : controller.getDeclaredMethods()) {
                assertNoEntity(method.getGenericReturnType());
                for (Type parameter : method.getGenericParameterTypes()) {
                    assertNoEntity(parameter);
                }
            }
        }
        for (Class<?> dto : List.of(BookDTO.class, MemberDTO.class, BorrowingDTO.class, ReservationDTO.class,
                BorrowingRequestDTO.class, ReservationRequestDTO.class, ErrorResponseDTO.class)) {
            for (var component : dto.getRecordComponents()) {
                assertNoEntity(component.getGenericType());
                Schema schema = component.getAccessor().getAnnotation(Schema.class);
                if (schema == null) {
                    schema = dto.getDeclaredFields()[List.of(dto.getRecordComponents()).indexOf(component)]
                            .getAnnotation(Schema.class);
                }
                assertNotNull(schema, dto.getSimpleName() + "." + component.getName());
                assertFalse(schema.example().isBlank());
            }
        }
    }

    @Test
    void openApi_shouldDocumentAllFourteenOperationsAndDtoSchemas() throws Exception {
        String response = mockMvc.perform(get("/api-docs")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode document = objectMapper.readTree(response);
        Map<String, List<String>> expected = Map.of(
                "/books", List.of("get", "post"), "/books/{id}", List.of("get", "put", "delete"),
                "/members", List.of("get", "post"), "/members/{id}", List.of("get"),
                "/borrowings", List.of("post"), "/borrowings/{id}/return", List.of("put"),
                "/members/{id}/borrowings", List.of("get"), "/reservations", List.of("post"),
                "/members/{memberId}/reservations", List.of("get"),
                "/members/{memberId}/reservations/{reservationId}", List.of("delete"));
        int operationCount = 0;
        for (var entry : expected.entrySet()) {
            for (String verb : entry.getValue()) {
                JsonNode operation = document.path("paths").path(entry.getKey()).path(verb);
                assertFalse(operation.isMissingNode(), verb + " " + entry.getKey());
                assertFalse(operation.path("summary").asText().isBlank());
                assertFalse(operation.path("tags").isEmpty());
                assertTrue(operation.path("responses").has("500"));
                operationCount++;
            }
        }
        assertEquals(14, operationCount);
        for (String name : List.of("BookDTO", "MemberDTO", "BorrowingDTO", "ReservationDTO")) {
            assertTrue(document.path("components").path("schemas").has(name), name);
        }
        for (String name : List.of("Book", "Member", "Borrowing", "Reservation")) {
            assertFalse(document.path("components").path("schemas").has(name), name);
        }
    }

    @Test
    void swaggerUi_shouldBeAvailableAtAssignedPath() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Swagger UI")));
    }

    private void assertNoEntity(Type type) {
        assertFalse(type.getTypeName().contains("com.example.libraryapi.entity"), type.getTypeName());
        if (type instanceof ParameterizedType parameterized) {
            for (Type argument : parameterized.getActualTypeArguments()) {
                assertNoEntity(argument);
            }
        }
    }
}
