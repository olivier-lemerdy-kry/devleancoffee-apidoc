package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record EventCreationRequest(
    @NotBlank @Size(max = SIZE_TITLE) String title,
    @NotNull LocalDateTime start,
    @NotNull LocalDateTime end) {

  public EventCreationRequest {
    if (start.isAfter(end)) {
      throw new StartIsAfterEndException(start, end);
    }
  }
}
