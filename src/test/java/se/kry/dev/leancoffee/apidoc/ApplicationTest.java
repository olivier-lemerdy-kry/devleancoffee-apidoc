package se.kry.dev.leancoffee.apidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.kry.dev.leancoffee.apidoc.data.Event;
import se.kry.dev.leancoffee.apidoc.data.EventRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EventRepository repository;

  @Test
  @Order(1)
  void create_event() throws Exception {
    assertThat(repository.count()).isZero();

    var payload = objectMapper.createObjectNode()
        .put("title", "Some event")
        .put("start", "2001-01-01T00:00:00")
        .put("end", "2001-01-01T12:00:00")
        .toString();

    mockMvc.perform(post("/api/v1/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00")
        );

    assertThat(repository.count()).isEqualTo(1);
  }

  @Test
  @Order(2)
  void read_events() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/api/v1/events"))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.content").isArray(),
            jsonPath("$.content[0].title").value("Some event"),
            jsonPath("$.content[0].start").value("2001-01-01T00:00:00"),
            jsonPath("$.content[0].end").value("2001-01-01T12:00:00")
        );
  }

  @Test
  @Order(3)
  void update_event() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .put("start", "2001-01-01T01:00:00")
        .put("end", "2001-01-01T13:00:00")
        .toString();

    mockMvc.perform(patch("/api/v1/events/{id}", findFirstEventId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00")
        );
  }

  @Test
  @Order(4)
  void read_event() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/api/v1/events/{id}", findFirstEventId()))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00")
        );
  }

  @Test
  @Order(5)
  void delete_event() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(delete("/api/v1/events/{id}", findFirstEventId()))
        .andExpect(status().isOk());

    assertThat(repository.count()).isZero();
  }

  private UUID findFirstEventId() {
    return repository.findAll(Pageable.ofSize(1))
        .stream().findFirst()
        .map(Event::getId)
        .orElseThrow();
  }
}
