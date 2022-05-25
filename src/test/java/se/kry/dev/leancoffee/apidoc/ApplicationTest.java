package se.kry.dev.leancoffee.apidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se.kry.dev.leancoffee.apidoc.data.EventRepository;
import se.kry.dev.leancoffee.apidoc.domain.EventConstants;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.kry.se", uriPort = 443)
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

    var result = mockMvc.perform(post("/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.title").value("Some event"),
            jsonPath("$.start").value("2001-01-01T00:00:00"),
            jsonPath("$.end").value("2001-01-01T12:00:00"),
            jsonPath("$._links").isMap(),
            jsonPath("$._links.self").isMap(),
            jsonPath("$._links.self.href").value(startsWith("https://dev.kry.se/events/")))
        .andDo(document("step1_create_event",
            requestFields(
                titleField(),
                startField(),
                endField()
            ),
            responseFields(
                idField(),
                titleField(),
                startField(),
                endField(),
                linksSubsection()
            )))
        .andReturn();

    assertThat(repository.count()).isEqualTo(1);

    return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
  }

  void step2_read_events() throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/events"))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$._embedded").isMap(),
            jsonPath("$._embedded.events").isArray(),
            jsonPath("$._embedded.events[0].title").value("Some event"),
            jsonPath("$._embedded.events[0].start").value("2001-01-01T00:00:00"),
            jsonPath("$._embedded.events[0].end").value("2001-01-01T12:00:00"),
            jsonPath("$._embedded.events[0]._links").isMap(),
            jsonPath("$._embedded.events[0]._links.self").isMap(),
            jsonPath("$._embedded.events[0]._links.self.href").value(startsWith("https://dev.kry.se/events/")))
        .andDo(document("step2_read_events",
            requestParameters(
                pageParameter(),
                pageSizeParameter()
            ),
            responseFields(
                pageSubsection(),
                embeddedSubsection(),
                linksSubsection()
            ).andWithPrefix("page.",
                fieldWithPath("size").description("Page size"),
                fieldWithPath("totalElements").description("Total number of elements in the collection"),
                fieldWithPath("totalPages").description("Total number of pages in the collection"),
                fieldWithPath("number").description("Page number, 0-based"))));
  }

  void step3_update_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    var payload = objectMapper.createObjectNode()
        .put("title", "Some other event")
        .put("start", "2001-01-01T01:00:00")
        .put("end", "2001-01-01T13:00:00")
        .toString();

    mockMvc.perform(patch("/events/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00"),
            jsonPath("$._links").isMap(),
            jsonPath("$._links.self").isMap(),
            jsonPath("$._links.self.href").value(startsWith("https://dev.kry.se/events/")))
        .andDo(document("step3_update_event",
            pathParameters(
                idParameter()
            ),
            requestFields(
                titleField().optional(),
                startField().optional(),
                endField().optional()
            ),
            responseFields(
                idField(),
                titleField(),
                startField(),
                endField(),
                linksSubsection(),
                templatesSubsection()
            )));
  }

  void step4_read_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(get("/events/{id}", id))
        .andExpectAll(
            jsonPath("$.title").value("Some other event"),
            jsonPath("$.start").value("2001-01-01T01:00:00"),
            jsonPath("$.end").value("2001-01-01T13:00:00"),
            jsonPath("$._links").isMap(),
            jsonPath("$._links.self").isMap(),
            jsonPath("$._links.self.href").value(startsWith("https://dev.kry.se/events/")))
        .andDo(document("step4_read_event",
            pathParameters(
                idParameter()
            ),
            responseFields(
                idField(),
                titleField(),
                startField(),
                endField(),
                linksSubsection(),
                templatesSubsection()
            )));
  }

  void step5_delete_event(UUID id) throws Exception {
    assertThat(repository.count()).isEqualTo(1);

    mockMvc.perform(delete("/events/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(document("step5_delete_event",
            pathParameters(
                idParameter()
            )));

    assertThat(repository.count()).isZero();
  }

  private RestDocumentationResultHandler document(String identifier, Snippet... snippets) {
    return MockMvcRestDocumentation.document(identifier,
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        snippets);
  }

  private ParameterDescriptor idParameter() {
    return parameterWithName("id").description("Event unique identifier (UUID)");
  }

  private ParameterDescriptor pageParameter() {
    return parameterWithName("page").description("Page number, 0-based").optional();
  }

  private ParameterDescriptor pageSizeParameter() {
    return parameterWithName("size").description("Page size").optional();
  }

  private FieldDescriptor idField() {
    return fieldWithPath("id").description("Event unique identifier (UUID), generated by the service");
  }

  private FieldDescriptor titleField() {
    return fieldWithPath("title").description(
        String.format(
            "Title describing what this event is about, must not be empty or longer than %s characters",
            EventConstants.SIZE_TITLE));
  }

  private FieldDescriptor startField() {
    return fieldWithPath("start").description("Start date time of this event");
  }

  private FieldDescriptor endField() {
    return fieldWithPath("end").description("End date time of this event, required to be after start");
  }

  private FieldDescriptor pageSubsection() {
    return subsectionWithPath("page").description("Embedded resources");
  }

  private FieldDescriptor embeddedSubsection() {
    return subsectionWithPath("_embedded").description("Embedded resources");
  }

  private FieldDescriptor linksSubsection() {
    return subsectionWithPath("_links").description("Links to this resource");
  }

  private FieldDescriptor templatesSubsection() {
    return subsectionWithPath("_templates").description("HAL Forms resources");
  }
}
