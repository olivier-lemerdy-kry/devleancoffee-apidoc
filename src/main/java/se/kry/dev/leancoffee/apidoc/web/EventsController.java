package se.kry.dev.leancoffee.apidoc.web;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import se.kry.dev.leancoffee.apidoc.domain.EventCreationRequest;
import se.kry.dev.leancoffee.apidoc.domain.EventResponse;
import se.kry.dev.leancoffee.apidoc.domain.EventUpdateRequest;
import se.kry.dev.leancoffee.apidoc.services.EventService;

@RestController
@RequestMapping("events")
@ExposesResourceFor(EventResponse.class)
public class EventsController {

  private final EventService service;

  public EventsController(EventService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  EntityModel<EventResponse> createEvent(@Valid @RequestBody EventCreationRequest eventCreationRequest) {
    return EntityModel.of(service.createEvent(eventCreationRequest));
  }

  @GetMapping
  PagedModel<EntityModel<EventResponse>> readEvents(
      Pageable pageable,
      PagedResourcesAssembler<EventResponse> pagedResourcesAssembler) {
    return pagedResourcesAssembler.toModel(service.getEvents(pageable));
  }

  @GetMapping("{id}")
  ResponseEntity<EntityModel<EventResponse>> readEvent(@PathVariable UUID id) {
    return service.getEvent(id)
        .map(EntityModel::of)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("{id}")
  ResponseEntity<EntityModel<EventResponse>> updateEvent(
      @PathVariable UUID id,
      @Valid @RequestBody EventUpdateRequest eventUpdateRequest) {
    return service.updateEvent(id, eventUpdateRequest)
        .map(EntityModel::of)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  void deleteEvent(@PathVariable UUID id) {
    service.deleteEvent(id);
  }
}
