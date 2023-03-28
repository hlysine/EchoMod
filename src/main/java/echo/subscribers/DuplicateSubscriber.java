package echo.subscribers;

public interface DuplicateSubscriber {
    default void afterDuplicateStart() {
    }

    default void afterDuplicateEnd() {
    }
}
