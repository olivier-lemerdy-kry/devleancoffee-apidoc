package se.kry.dev.leancoffee.apidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se.kry.dev.leancoffee.apidoc.data.EventRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.kry.se")
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
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00"))
        .andDo(document("POST-events", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
        .andReturn();

    assertThat(repository.count()).isEqualTo(1);

    return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
  }

  void step2_read_events() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/api/v1/events"))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$._embedded").isMap(),
            jsonPath("$._embedded.events").isArray(),
            jsonPath("$._embedded.events[0].title").value("Some event"),
            jsonPath("$._embedded.events[0].start").value("2001-01-01T00:00:00"),
            jsonPath("$._embedded.events[0].end").value("2001-01-01T12:00:00"))
        .andDo(document("GET-events", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
  }

  void step3_update_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .put("start", "2001-01-01T01:00:00")
        .put("end", "2001-01-01T13:00:00")
        .toString();

    mockMvc.perform(patch("/api/v1/events/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00"))
        .andDo(document("PATCH-events-ID", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
  }

  void step4_read_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/api/v1/events/{id}", id))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00"))
        .andDo(document("GET-events-ID", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
  }

  void step5_delete_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(delete("/api/v1/events/{id}", id))
        .andExpect(status().isOk())
        .andDo(document("DELETE-events-ID", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    assertThat(repository.count()).isZero();
  }
}
