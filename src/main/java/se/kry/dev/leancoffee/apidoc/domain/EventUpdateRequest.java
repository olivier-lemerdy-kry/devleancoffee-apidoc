package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.validation.constraints.Size;
import org.springframework.hateoas.InputType;
import org.springframework.hateoas.mediatype.html.HtmlInputType;

public record EventUpdateRequest(
    Optional<@Size(max = SIZE_TITLE) String> title,
    @InputType(HtmlInputType.DATETIME_LOCAL_VALUE) Optional<LocalDateTime> start,
    @InputType(HtmlInputType.DATETIME_LOCAL_VALUE) Optional<LocalDateTime> end) {

  public EventUpdateRequest {
    start.ifPresent(s -> end.ifPresent(e -> {
      if (s.isAfter(e)) {
        throw new StartIsAfterEndException(s, e);
      }
    }));
  }
}
