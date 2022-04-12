package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.validation.constraints.Size;

public record EventUpdateRequest(
    Optional<@Size(max = SIZE_TITLE) String> title,
    Optional<LocalDateTime> start,
    Optional<LocalDateTime> end) {

  public EventUpdateRequest {
    start.ifPresent(s -> end.ifPresent(e -> {
      if (s.isAfter(e)) {
        throw new StartIsAfterEndException(s, e);
      }
    }));
  }
}
