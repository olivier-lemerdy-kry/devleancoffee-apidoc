package se.kry.dev.leancoffee.apidoc.domain;

import static se.kry.dev.leancoffee.apidoc.domain.EventConstants.SIZE_TITLE;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "events", itemRelation = "event")
public record EventResponse(@NotNull UUID id,
                            @NotBlank @Size(max = SIZE_TITLE) String title,
                            @NotNull LocalDateTime start,
                            @NotNull LocalDateTime end) {
}
