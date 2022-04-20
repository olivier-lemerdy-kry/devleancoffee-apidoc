package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import org.springframework.hateoas.InputType;
import org.springframework.hateoas.mediatype.html.HtmlInputType;

@Data
public class EventCreationRequest {

  @NonNull
  @NotBlank
  @Size(max = SIZE_TITLE)
  private String title;

  @NonNull
  @NotNull
  @InputType(HtmlInputType.DATETIME_LOCAL_VALUE)
  private LocalDateTime start;

  @NonNull
  @NotNull
  @InputType(HtmlInputType.DATETIME_LOCAL_VALUE)
  private LocalDateTime end;
}
