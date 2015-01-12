package services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import static akka.pattern.Patterns.ask;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;
import play.libs.F;
import rx.Observable;
import rx.subjects.PublishSubject;
import services.models.Attendee;
import services.models.Journey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A in-memory implementation of the JourneysService interface.
 */
public class JourneysServiceStub implements JourneysService {

    final ActorRef service;
    final PublishSubject<F.Tuple<Long, Attendee>> attendees;
    final Timeout t = new Timeout(1, TimeUnit.SECONDS);

    public JourneysServiceStub(ActorSystem ctx) {
        this.service = ctx.actorOf(Props.create(JourneyServiceActor.class));
        attendees = PublishSubject.create();
    }

    @Override
    public F.Promise<List<Journey>> allJourneys() {
        return F.Promise.wrap(ask(service, new AllJourneys(), t)).map(object -> (List<Journey>)object);
    }

    @Override
    public F.Promise<Boolean> join(Long journeyId, Long driverId, String attendeeName) {
        return F.Promise.wrap(ask(service, new JoinAttendee(journeyId, driverId, attendeeName), t)).map(object -> (Optional<Attendee>)object).map(added -> {
            return added.
                    map(attendee -> {
                        attendees.onNext(F.Tuple(journeyId, attendee));
                        return true;
                    }).
                    orElseGet(() -> false);
        });
    }

    @Override
    public F.Promise<Boolean> attend(Long journeyId, String attendeeName, Integer availableSeats) {
        return F.Promise.wrap(ask(service, new AttendJourney(journeyId, attendeeName, availableSeats), t)).map(object -> (Optional<Attendee>)object).map(added -> {
            return added.
                    map(attendee -> {
                        attendees.onNext(F.Tuple(journeyId, attendee));
                        return true;
                    }).
                    orElseGet(() -> false);
        });
    }

    public Observable<Attendee> attendees(Long journeyId) {
        return attendees.filter(t -> t._1.equals(journeyId)).map(t -> t._2);
    }

    public static class JourneyServiceActor extends AbstractActor {

        List<Journey> journeys;

        public JourneyServiceActor() {
            journeys = new ArrayList<>();
            journeys.add(new Journey(1L, "Software engineering seminar", new ArrayList<Attendee>() {{ add(new Attendee(1L, "Olivier Barais", 3)); add(new Attendee(2L, "Julien Richard-Foy", 2)); }}));
            receive(ReceiveBuilder.
                    match(AllJourneys.class, m -> sender().tell(new ArrayList<>(journeys), self())).
                    match(AttendJourney.class, m -> {
                        Optional<Attendee> result = journeys.stream().
                                filter(j -> j.id.equals(m.journeyId)).
                                findFirst().
                                map(j -> {
                                    Attendee attendee = new Attendee((long)j.attendees.size(), m.attendeeName, m.availableSeats);
                                    j.attendees.add(attendee);
                                    return Optional.of(attendee);
                                }).
                                orElseGet(Optional::empty);
                        sender().tell(result, self());
                    }).
                    match(JoinAttendee.class, m -> {
                        Optional<Attendee> result = journeys.stream().
                                filter(j -> j.id.equals(m.journeyId)).
                                findFirst().
                                flatMap(j -> {
                                    return j.attendees.stream().
                                            filter(a -> a.id.equals(m.attendeeId)).
                                            findFirst().
                                            filter(a -> a.availableSeats > 0).
                                            map(driver -> {
                                                Attendee attendee = new Attendee((long) j.attendees.size(), m.attendeeName, 0);
                                                j.attendees.add(attendee);
                                                j.attendees.set(j.attendees.indexOf(driver), new Attendee(driver.id, driver.name, driver.availableSeats - 1));
                                                return Optional.of(attendee);
                                            }).
                                            orElseGet(Optional::empty);
                                });
                        sender().tell(result, self());
                    }).build());
        }
    }

    static class AllJourneys {}
    static class JoinAttendee {
        public final Long journeyId;
        public final Long attendeeId;
        public final String attendeeName;
        JoinAttendee(Long journeyId, Long attendeeId, String attendeeName) {
            this.journeyId = journeyId;
            this.attendeeId = attendeeId;
            this.attendeeName = attendeeName;
        }
    }
    static class AttendJourney {
        public final Long journeyId;
        public final String attendeeName;
        public final Integer availableSeats;
        public AttendJourney(Long journeyId, String attendeeName, Integer availableSeats) {
            this.journeyId = journeyId;
            this.attendeeName = attendeeName;
            this.availableSeats = availableSeats;
        }
    }
}