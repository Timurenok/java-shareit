package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    private final UserDto userDto = new UserDto(1L, "user", "user@gmail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description",
            userDto, LocalDateTime.now(), null);

    private final List<ItemRequestDto> listItemRequestDto = new ArrayList<>();

    @Test
    void shouldSaveItemRequest() throws Exception {
        when(itemRequestService.save(any(Long.class), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldFindItemRequest() throws Exception {
        when(itemRequestService.findById(any(Long.class), any(Long.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldFindOwnItemRequests() throws Exception {
        when(itemRequestService.findByRequesterId(any(Long.class)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(listItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id",
                        is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldFindItemRequestsByPages() throws Exception {
        when(itemRequestService.findByPages(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(listItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id",
                        is(itemRequestDto.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
