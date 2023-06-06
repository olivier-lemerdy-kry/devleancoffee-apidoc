package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.InputType;
import org.springframework.hateoas.mediatype.html.HtmlInputType;

@AllArgsConstructor
@Data
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EventUpdateRequest {

  private Optional<@Size(max = SIZE_TITLE) String> title;

  @InputType(HtmlInputType.DATETIME_LOCAL_VALUE)
  private Optional<LocalDateTime> start;

  @InputType(HtmlInputType.DATETIME_LOCAL_VALUE)
  private Optional<LocalDateTime> end;
}
