package services.models;

import java.util.List;

/**
 * A journey is identified by `id`, it has a `name` and a list of attending people `attendees`.
 */
public class Journey {
    public final Long id;
    public final String name;
    public final List<Attendee> attendees;

    public Journey(Long id, String name, List<Attendee> attendees) {
        this.id = id;
        this.name = name;
        this.attendees = attendees;
    }
}