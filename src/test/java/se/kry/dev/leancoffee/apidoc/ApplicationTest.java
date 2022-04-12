package se.kry.dev.leancoffee.apidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se.kry.dev.leancoffee.apidoc.data.EventRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EventRepository repository;

  @Test
  @WithMockUser
  void scenario() throws Exception {
    UUID id = step1_create_event();
    step2_read_events();
    step3_update_event(id);
    step4_read_event(id);
    step5_delete_event(id);
  }

  UUID step1_create_event() throws Exception {
    assertThat(repository.count()).isZero();

    var payload = objectMapper.createObjectNode()
        .put("title", "Some event")
        .put("start", "2001-01-01T00:00:00")
        .put("end", "2001-01-01T12:00:00")
        .toString();

    var result = mockMvc.perform(post("/api/v1/events")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00")
        ).andReturn();

    assertThat(repository.count()).isEqualTo(1);

    return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
  }

  void step2_read_events() throws Exception {
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

  void step3_update_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .put("start", "2001-01-01T01:00:00")
        .put("end", "2001-01-01T13:00:00")
        .toString();

    mockMvc.perform(patch("/api/v1/events/{id}", id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00")
        );
  }

  void step4_read_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/api/v1/events/{id}", id))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00")
        );
  }

  void step5_delete_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(delete("/api/v1/events/{id}", id)
            .with(csrf()))
        .andExpect(status().isOk());

    assertThat(repository.count()).isZero();
  }
}
