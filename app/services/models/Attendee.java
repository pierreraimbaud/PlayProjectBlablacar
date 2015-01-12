package services.models;

/**
 * An Attendee is identified by `id`, he has a `name` and a number of available seats `availableSeats` (which is zero if this Attendee is not a driver)
 */
public class Attendee {
    public final Long id;
    public final String name;
    public final Integer availableSeats;

    public Attendee(Long id, String name, Integer availableSeats) {
        this.id = id;
        this.name = name;
        this.availableSeats = availableSeats;
    }
}