package se.kry.dev.leancoffee.apidoc.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se.kry.dev.leancoffee.apidoc.domain.EventResponse;
import se.kry.dev.leancoffee.apidoc.domain.EventUpdateRequest;
import se.kry.dev.leancoffee.apidoc.services.EventService;

@WebMvcTest(EventsController.class)
@WithMockUser
class EventsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EventService service;

  @Test
  void create_event() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);
    var end = start.plusHours(12);

    when(service.createEvent(any())).thenReturn(
        new EventResponse(uuid, "Some event", start, end));

    var payload = objectMapper.createObjectNode()
        .put("title", "someEvent")
        .put("start", "2001-01-01T00:00:00")
        .put("end", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.id").value("38a14a82-d5a2-4210-9d61-cc3577bfa5df"),
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00")
        );
  }

  @Test
  void create_event_with_blank_title() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", " ")
        .put("start", "2001-01-01T00:00:00")
        .put("end", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_event_with_too_long_title() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", "X".repeat(300))
        .put("start", "2001-01-01T00:00:00")
        .put("end", "2001-01-01T12:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_event_with_null_start() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", "Some event")
        .put("end", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_event_with_null_end() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", "Some event")
        .put("start", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_event_with_start_after_end() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", "Some event")
        .put("start", "2001-01-01T12:00:00")
        .put("end", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void read_events() throws Exception {
    var uuid1 = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");
    var start1 = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);
    var end1 = start1.plusHours(12);

    var uuid2 = UUID.fromString("8ebea9a7-e0ef-4a62-a729-aff26134f9d8");
    var start2 = start1.plusHours(1);
    var end2 = end1.plusHours(1);

    var content = List.of(
        new EventResponse(uuid1, "Some event", start1, end1),
        new EventResponse(uuid2, "Some other event", start2, end2)
    );

    var pageable = PageRequest.ofSize(20);

    when(service.getEvents(pageable))
        .thenReturn(new PageImpl<>(content, pageable, content.size()));

    mockMvc.perform(get("/events"))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$._embedded").isMap(),
            jsonPath("$._embedded.events").isArray(),
            jsonPath("$._embedded.events[0].id").value("38a14a82-d5a2-4210-9d61-cc3577bfa5df"),
            jsonPath("$._embedded.events[0].title").value("Some event"),
            jsonPath("$._embedded.events[0].start").value("2001-01-01T00:00:00"),
            jsonPath("$._embedded.events[0].end").value("2001-01-01T12:00:00"),
            jsonPath("$._embedded.events[1].id").value("8ebea9a7-e0ef-4a62-a729-aff26134f9d8"),
            jsonPath("$._embedded.events[1].title").value("Some other event"),
            jsonPath("$._embedded.events[1].start").value("2001-01-01T01:00:00"),
            jsonPath("$._embedded.events[1].end").value("2001-01-01T13:00:00"),
            jsonPath("$._links").isMap(),
            jsonPath("$._links.self").isMap(),
            jsonPath("$._links.self.href").value("http://localhost/events?page=0&size=20"),
            jsonPath("$.page").isMap(),
            jsonPath("$.page.size").value(20),
            jsonPath("$.page.totalElements").value(2),
            jsonPath("$.page.totalPages").value(1),
            jsonPath("$.page.number").value(0)
        );
  }

  @Test
  void read_event() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    when(service.getEvent(uuid)).thenReturn(
        Optional.of(new EventResponse(uuid, "Some event", start, start.plusHours(12))));

    mockMvc.perform(get("/events/{id}", uuid))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00")
        );
  }

  @Test
  void update_event() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .toString();

    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    when(service.updateEvent(uuid,
        new EventUpdateRequest(Optional.of("Some other event"), Optional.empty(), Optional.empty())))
        .thenReturn(Optional.of(new EventResponse(uuid, "Some other event", start, start.plusHours(12))));

    mockMvc.perform(patch("/events/{id}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Some other event"));
  }

  @Test
  void update_event_with_incorrect_id() throws Exception {
    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .toString();

    mockMvc.perform(patch("/events/{id}", "foobar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_event_with_unknown_id() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .toString();

    mockMvc.perform(patch("/events/{id}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_event_with_too_long_title() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");

    var payload = objectMapper.createObjectNode()
        .put("title", "X".repeat(300))
        .toString();

    mockMvc.perform(patch("/events/{id}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_event_with_start_after_end() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");

    var payload = objectMapper.createObjectNode()
        .put("start", "2001-01-01T12:00:00")
        .put("end", "2001-01-01T00:00:00")
        .toString();

    mockMvc.perform(patch("/events/{id}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_event() throws Exception {
    var uuid = UUID.fromString("38a14a82-d5a2-4210-9d61-cc3577bfa5df");

    mockMvc.perform(delete("/events/{id}", uuid))
        .andExpect(status().isOk());
  }

  @Test
  void delete_event_with_incorrect_id() throws Exception {
    mockMvc.perform(delete("/events/{id}", "foobar"))
        .andExpect(status().isBadRequest());
  }
}