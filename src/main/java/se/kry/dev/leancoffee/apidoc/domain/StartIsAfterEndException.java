package se.kry.dev.leancoffee.apidoc.domain;

import java.time.LocalDateTime;
import lombok.Getter;

public class StartIsAfterEndException extends IllegalArgumentException {

  @Getter
  private final LocalDateTime start;
  @Getter
  private final LocalDateTime end;

  public StartIsAfterEndException(LocalDateTime start, LocalDateTime end) {
    super(String.format("Start %s is after end %s", start, end));
    this.start = start;
    this.end = end;
  }
}
