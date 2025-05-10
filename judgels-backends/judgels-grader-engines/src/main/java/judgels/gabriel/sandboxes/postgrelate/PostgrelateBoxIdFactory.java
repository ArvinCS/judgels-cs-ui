package judgels.gabriel.sandboxes.postgrelate;

public class PostgrelateBoxIdFactory {
    private static int boxId;

    private PostgrelateBoxIdFactory() {}

    static synchronized int newBoxId() {
        int currentBoxId = boxId;
        boxId = (boxId + 1) % 100;

        return currentBoxId;
    }
}
