package se.kry.dev.leancoffee.apidoc.services;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.kry.dev.leancoffee.apidoc.data.Event;
import se.kry.dev.leancoffee.apidoc.data.EventRepository;
import se.kry.dev.leancoffee.apidoc.domain.EventCreationRequest;
import se.kry.dev.leancoffee.apidoc.domain.EventResponse;
import se.kry.dev.leancoffee.apidoc.domain.EventUpdateRequest;

@Service
public class EventService {

  private final EventRepository repository;

  public EventService(EventRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public EventResponse createEvent(@NotNull EventCreationRequest eventCreationRequest) {
    return responseFromEvent(repository.save(newEventFromCreationRequest(eventCreationRequest)));
  }

  public Page<EventResponse> getEvents(@NotNull Pageable pageable) {
    return repository.findAll(pageable).map(this::responseFromEvent);
  }

  public Optional<EventResponse> getEvent(@NotNull UUID id) {
    return repository.findById(id).map(this::responseFromEvent);
  }

  public Optional<EventResponse> updateEvent(@NotNull UUID id, @NotNull EventUpdateRequest eventUpdateRequest) {
    return repository.findById(id)
        .map(event -> updateEventFromUpdateRequest(event, eventUpdateRequest))
        .map(repository::save)
        .map(this::responseFromEvent);
  }

  @Transactional
  public void deleteEvent(@NotNull UUID id) {
    repository.deleteById(id);
  }

  private Event newEventFromCreationRequest(@NotNull EventCreationRequest eventCreationRequest) {
    return new Event()
        .setTitle(eventCreationRequest.getTitle())
        .setStart(eventCreationRequest.getStart())
        .setEnd(eventCreationRequest.getEnd());
  }

  private Event updateEventFromUpdateRequest(@NotNull Event event, @NotNull EventUpdateRequest eventUpdateRequest) {
    eventUpdateRequest.getTitle().ifPresent(event::setTitle);
    eventUpdateRequest.getStart().ifPresent(event::setStart);
    eventUpdateRequest.getEnd().ifPresent(event::setEnd);
    return event;
  }

  private EventResponse responseFromEvent(Event event) {
    return new EventResponse(requireNonNull(event.getId()), event.getTitle(), event.getStart(), event.getEnd());
  }
}
