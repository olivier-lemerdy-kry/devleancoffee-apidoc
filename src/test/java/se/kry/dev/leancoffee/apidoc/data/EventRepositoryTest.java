package se.kry.dev.leancoffee.apidoc.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class EventRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private EventRepository repository;

  @Test
  void get_event() {
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    var id =
        entityManager.persistAndGetId(new Event().setTitle("Some event").setStart(start).setEnd(start.plusHours(12)),
            UUID.class);

    var event = repository.findById(id);
    assertThat(event)
        .isNotEmpty()
        .hasValueSatisfying(e -> {
          assertThat(e.getId()).isEqualTo(id);
          assertThat(e.getTitle()).isEqualTo("Some event");
          assertThat(e.getStart()).hasToString("2001-01-01T00:00");
          assertThat(e.getEnd()).hasToString("2001-01-01T12:00");
        });
  }

  @Test
  void get_events() {
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    IntStream.range(0, 50)
        .mapToObj(i -> new Event().setTitle("Event" + i).setStart(start.plusDays(i)).setEnd(start.plusDays(i).plusHours(12)))
        .forEach(entityManager::persist);

    var events = repository.findAll(Pageable.ofSize(20));
    assertThat(events).hasSize(20);
    assertThat(events.getTotalElements()).isEqualTo(50);
    assertThat(events.getTotalPages()).isEqualTo(3);
    assertThat(events.getNumber()).isZero();
    assertThat(events.getNumberOfElements()).isEqualTo(20);
    assertThat(events.getSize()).isEqualTo(20);
  }

  @Test
  void save_event() {
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);
    var event = repository.save(new Event().setTitle("Some event").setStart(start).setEnd(start.plusHours(12)));

    assertThat(event).isNotNull();
    assertThat(event.getId()).isNotNull();
    assertFalse(event.isNew());
    assertThat(event.getTitle()).isEqualTo("Some event");
    assertThat(event.getStart()).hasToString("2001-01-01T00:00");
    assertThat(event.getEnd()).hasToString("2001-01-01T12:00");
  }

  @Test
  void save_event_with_blank_title() {
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    var exception = assertThrows(ConstraintViolationException.class, () ->
        repository.saveAndFlush(new Event().setTitle(" ").setStart(start).setEnd(start.plusHours(12))));

    assertThat(exception.getConstraintViolations()).hasSize(1);
  }

  @Test
  void save_event_with_too_long_title() {
    var title = "X".repeat(300);
    var start = LocalDate.of(2001, Month.JANUARY, 1).atTime(LocalTime.MIDNIGHT);

    var exception = assertThrows(ConstraintViolationException.class, () ->
        repository.saveAndFlush(new Event().setTitle(title).setStart(start).setEnd(start.plusHours(12))));

    assertThat(exception.getConstraintViolations()).hasSize(1);
  }
}
