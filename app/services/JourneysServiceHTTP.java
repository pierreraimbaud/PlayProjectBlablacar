package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.F;
import play.libs.ws.WSClient;
import rx.Observable;
import scala.NotImplementedError;
import services.models.Attendee;
import services.models.Journey;

import java.util.List;

/**
 * JourneyService implementation that delegates to a third party JSON Web service
 */
public class JourneysServiceHTTP implements JourneysService {

    /** The HTTP client used to communicate with the Web service */
    final WSClient client;
    /** A JSON object mapper to handle JSON serialization/deserialization */
    final ObjectMapper mapper;
    /** The Web service base URL */
    final static String API_URL = "http://localhost:8080";

    public JourneysServiceHTTP(WSClient client) {
        this.client = client;
        mapper = new ObjectMapper();
    }

    @Override
    public F.Promise<List<Journey>> allJourneys() {
        // Example of implementation performing a GET request to the `/journeys` endpoint and interpreting the result as
        // a list of Journey values.
        return client.url(API_URL + "/journeys")
                .get()
                .map(r -> mapper.readValue(r.getBody(), new TypeReference<List<Journey>>() {}));
    }

    @Override
    public F.Promise<Boolean> join(Long journeyId, Long driverId, String attendeeName) {
        throw new NotImplementedError();
    }

    @Override
    public F.Promise<Boolean> attend(Long journeyId, String attendeeName, Integer availableSeats) {
        throw new NotImplementedError();
    }

    @Override
    public Observable<Attendee> attendees(Long journeyId) {
        throw new NotImplementedError();
    }
}