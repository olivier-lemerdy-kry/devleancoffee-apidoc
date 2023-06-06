package se.kry.dev.leancoffee.apidoc.data;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Accessors(chain = true)
@Getter
@Setter
public class Event extends AbstractPersistable<UUID> {

  @NotBlank
  @Size(max = SIZE_TITLE)
  private String title;

  @NotNull
  private LocalDateTime start;

  @NotNull
  private LocalDateTime end;
}
