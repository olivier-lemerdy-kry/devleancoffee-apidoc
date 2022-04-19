package se.kry.dev.leancoffee.apidoc.web;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  ResponseEntity<EntityModel<EventResponse>> createEvent(@Valid @RequestBody EventCreationRequest eventCreationRequest) {
    var event = service.createEvent(eventCreationRequest);
    var link = readEventLink(event.id());
    return ResponseEntity.created(link.toUri()).body(EntityModel.of(event).add(link));
  }

  @GetMapping
  PagedModel<EntityModel<EventResponse>> readEvents(
      Pageable pageable,
      PagedResourcesAssembler<EventResponse> pagedResourcesAssembler) {
    return pagedResourcesAssembler.toModel(service.getEvents(pageable),
        new SimpleRepresentationModelAssembler<>() {
          @Override
          public void addLinks(EntityModel<EventResponse> resource) {
            resource.add(readEventLink(requireNonNull(resource.getContent()).id()));
          }

          @Override
          public void addLinks(CollectionModel<EntityModel<EventResponse>> resources) {
// Empty for now
          }
        });
  }

  @GetMapping("{id}")
  ResponseEntity<EntityModel<EventResponse>> readEvent(@PathVariable UUID id) {
    return service.getEvent(id)
        .map(EntityModel::of)
        .map(entity -> entity.add(
            readEventLink(id)
                .andAffordance(updateEventAffordance(id))
                .andAffordance(deleteEventAffordance(id))))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("{id}")
  ResponseEntity<EntityModel<EventResponse>> updateEvent(
      @PathVariable UUID id,
      @Valid @RequestBody EventUpdateRequest eventUpdateRequest) {
    return service.updateEvent(id, eventUpdateRequest)
        .map(EntityModel::of)
        .map(entity -> entity.add(readEventLink(id)
            .andAffordance(updateEventAffordance(id))
            .andAffordance(deleteEventAffordance(id))))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
    service.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }

  private Link readEventLink(UUID id) {
    return linkTo(methodOn(EventsController.class).readEvent(id)).withSelfRel();
  }

  private Affordance updateEventAffordance(UUID id) {
    return afford(methodOn(EventsController.class).updateEvent(id, null));
  }

  private Affordance deleteEventAffordance(UUID id) {
    return afford(methodOn(EventsController.class).deleteEvent(id));
  }
}
