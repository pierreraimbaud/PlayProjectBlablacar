package adapters;

import org.zeromq.ZMQ;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Makes an Observable<byte[]> from a ZMQ.Socket.
 *
 * Use it as follows:
 *
 * <pre>
 *     // Setup the socket by yourself (it should be a socket of type SUB or PULL)
 *     ZMQ.Socket subscriber = …;
 *     // Create the adapter
 *     ZeroMQObservableAdapter adapter = new ZeroMQObservableAdapter(subscriber);
 *
 *     // Do something with the observable (e.g. call its subscribe or map method)
 *     adapter.observable.…;
 *
 *     // … then when you want to stop listening to the socket, dispose the adapter and then close the socket
 *     adapter.dispose();
 *     subscriber.close();
 * </pre>
 *
 * /!\ WARNING /!\
 * Keep in mind that ZeroMQ sockets are not thread-safe, so don’t use the socket unless you have disposed the adapter.
 * (The adapter listens to the socket in a background thread, so this thread has to be stopped before you use the socket in another thread)
 */
public class ZeroMQObservableAdapter {

    private final Thread thread;

    /**
     * The stream of messages received from the socket.
     */
    public final Observable<byte[]> observable;

    /**
     * @param source A correctly configured socket so that the implementation can just call `recv` on it.
     */
    public ZeroMQObservableAdapter(ZMQ.Socket source) {
        PublishSubject<byte[]> subject = PublishSubject.create();

        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] msg = source.recv(ZMQ.DONTWAIT);
                if (msg != null) {
                    subject.onNext(msg);
                }
            }
        });
        thread.start();

        observable = Observable.create(subject::subscribe);
    }

    /**
     * Stop receiving messages from the socket.
     * You must call this method before closing the socket.
     */
    public void dispose() {
        thread.interrupt();
    }

}
