package se.kry.dev.leancoffee.apidoc.infra.web.hateoas;

import static java.util.Objects.requireNonNull;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.stereotype.Component;
import se.kry.dev.leancoffee.apidoc.domain.EventResponse;

@Component
public class EventResponseProcessor implements RepresentationModelProcessor<EntityModel<EventResponse>> {

  private final TypedEntityLinks<EventResponse> entityLinks;

  public EventResponseProcessor(EntityLinks entityLinks) {
    this.entityLinks = entityLinks.forType(EventResponse::id);
  }

  @Override
  public EntityModel<EventResponse> process(EntityModel<EventResponse> model) {
    var content = requireNonNull(model.getContent());
    return model.add(entityLinks.linkToItemResource(content));
  }
}
