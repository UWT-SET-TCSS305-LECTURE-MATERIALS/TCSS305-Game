package edu.uw.tcss.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Game class.
 * Tests both game logic and Flow API event publishing.
 *
 * @author Charles Bryan
 * @version Winter 2025
 */
@DisplayName("Game Class Tests")
class GameTest {

    private Game game;
    private TestSubscriber subscriber;

    @BeforeEach
    void setUp() {
        game = new Game();
        subscriber = new TestSubscriber();
    }

    // ==================== Initialization Tests ====================

    @Test
    @DisplayName("Game should initialize with piece at origin (0,0)")
    void testInitialPosition() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();

        subscriber.awaitEvent();

        GameEvent event = subscriber.getLastEvent();
        assertInstanceOf(GameEvent.NewGameEvent.class, event);

        GameEvent.NewGameEvent newGameEvent = (GameEvent.NewGameEvent) event;
        assertEquals(0, newGameEvent.startingLocation().x());
        assertEquals(0, newGameEvent.startingLocation().y());
    }

    @Test
    @DisplayName("New game should provide valid moves (DOWN and RIGHT from origin)")
    void testNewGameValidMoves() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();

        subscriber.awaitEvent();

        GameEvent.NewGameEvent event = (GameEvent.NewGameEvent) subscriber.getLastEvent();
        GameControls.ValidMoves validMoves = event.validMoves();

        assertFalse(validMoves.moves().get(GameControls.Move.UP), "UP should be invalid at origin");
        assertTrue(validMoves.moves().get(GameControls.Move.DOWN), "DOWN should be valid at origin");
        assertFalse(validMoves.moves().get(GameControls.Move.LEFT), "LEFT should be invalid at origin");
        assertTrue(validMoves.moves().get(GameControls.Move.RIGHT), "RIGHT should be valid at origin");
    }

    // ==================== Valid Move Tests ====================

    @Test
    @DisplayName("moveRight() should move piece from (0,0) to (1,0)")
    void testMoveRight() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveRight();
        subscriber.awaitEvent();

        GameEvent event = subscriber.getLastEvent();
        assertInstanceOf(GameEvent.MoveEvent.class, event);

        GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) event;
        assertEquals(GameControls.Move.RIGHT, moveEvent.direction());
        assertEquals(1, moveEvent.newLocation().x());
        assertEquals(0, moveEvent.newLocation().y());
    }

    @Test
    @DisplayName("moveDown() should move piece from (0,0) to (0,1)")
    void testMoveDown() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveDown();
        subscriber.awaitEvent();

        GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.DOWN, moveEvent.direction());
        assertEquals(0, moveEvent.newLocation().x());
        assertEquals(1, moveEvent.newLocation().y());
    }

    @Test
    @DisplayName("moveUp() from (0,1) should move piece to (0,0)")
    void testMoveUp() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveDown(); // Move to (0,1)
        subscriber.awaitEvent();

        game.moveUp(); // Move back to (0,0)
        subscriber.awaitEvent();

        GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.UP, moveEvent.direction());
        assertEquals(0, moveEvent.newLocation().x());
        assertEquals(0, moveEvent.newLocation().y());
    }

    @Test
    @DisplayName("moveLeft() from (1,0) should move piece to (0,0)")
    void testMoveLeft() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveRight(); // Move to (1,0)
        subscriber.awaitEvent();

        game.moveLeft(); // Move back to (0,0)
        subscriber.awaitEvent();

        GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.LEFT, moveEvent.direction());
        assertEquals(0, moveEvent.newLocation().x());
        assertEquals(0, moveEvent.newLocation().y());
    }

    // ==================== Invalid Move Tests ====================

    @Test
    @DisplayName("moveUp() from origin should generate InvalidMoveEvent")
    void testInvalidMoveUp() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveUp(); // Invalid - already at top
        subscriber.awaitEvent();

        GameEvent event = subscriber.getLastEvent();
        assertInstanceOf(GameEvent.InvalidMoveEvent.class, event);

        GameEvent.InvalidMoveEvent invalidEvent = (GameEvent.InvalidMoveEvent) event;
        assertEquals(GameControls.Move.UP, invalidEvent.attemptedDirection());
        assertEquals(0, invalidEvent.currentLocation().x());
        assertEquals(0, invalidEvent.currentLocation().y());
    }

    @Test
    @DisplayName("moveLeft() from origin should generate InvalidMoveEvent")
    void testInvalidMoveLeft() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveLeft(); // Invalid - already at left edge
        subscriber.awaitEvent();

        GameEvent.InvalidMoveEvent invalidEvent = (GameEvent.InvalidMoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.LEFT, invalidEvent.attemptedDirection());
    }

    @Test
    @DisplayName("moveRight() at right boundary should generate InvalidMoveEvent")
    void testInvalidMoveRightAtBoundary() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        // Move to right edge (x = 7)
        for (int i = 0; i < Game.WIDTH; i++) {
            game.moveRight();
            subscriber.awaitEvent();
        }

        // Try to move right again (invalid)
        game.moveRight();
        subscriber.awaitEvent();

        GameEvent.InvalidMoveEvent invalidEvent = (GameEvent.InvalidMoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.RIGHT, invalidEvent.attemptedDirection());
        assertEquals(Game.WIDTH - 1, invalidEvent.currentLocation().x());
    }

    @Test
    @DisplayName("moveDown() at bottom boundary should generate InvalidMoveEvent")
    void testInvalidMoveDownAtBoundary() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        // Move to bottom edge (y = 9)
        for (int i = 0; i < Game.HEIGHT; i++) {
            game.moveDown();
            subscriber.awaitEvent();
        }

        // Try to move down again (invalid)
        game.moveDown();
        subscriber.awaitEvent();

        GameEvent.InvalidMoveEvent invalidEvent = (GameEvent.InvalidMoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.DOWN, invalidEvent.attemptedDirection());
        assertEquals(Game.HEIGHT - 1, invalidEvent.currentLocation().y());
    }

    // ==================== Boundary Tests ====================

    @Test
    @DisplayName("Valid moves should be correct at top-left corner (0,0)")
    void testValidMovesAtTopLeftCorner() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent();

        GameEvent.NewGameEvent event = (GameEvent.NewGameEvent) subscriber.getLastEvent();
        GameControls.ValidMoves validMoves = event.validMoves();

        assertFalse(validMoves.moves().get(GameControls.Move.UP));
        assertTrue(validMoves.moves().get(GameControls.Move.DOWN));
        assertFalse(validMoves.moves().get(GameControls.Move.LEFT));
        assertTrue(validMoves.moves().get(GameControls.Move.RIGHT));
    }

    @Test
    @DisplayName("Valid moves should be correct at bottom-right corner (7,9)")
    void testValidMovesAtBottomRightCorner() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        // Move to bottom-right corner
        for (int i = 0; i < Game.WIDTH - 1; i++) {
            game.moveRight();
            subscriber.awaitEvent();
        }
        for (int i = 0; i < Game.HEIGHT - 1; i++) {
            game.moveDown();
            subscriber.awaitEvent();
        }

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        GameControls.ValidMoves validMoves = event.validMoves();

        assertTrue(validMoves.moves().get(GameControls.Move.UP));
        assertFalse(validMoves.moves().get(GameControls.Move.DOWN));
        assertTrue(validMoves.moves().get(GameControls.Move.LEFT));
        assertFalse(validMoves.moves().get(GameControls.Move.RIGHT));
    }

    @Test
    @DisplayName("Valid moves should be correct at center (4,5)")
    void testValidMovesAtCenter() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        // Move to center
        for (int i = 0; i < 4; i++) {
            game.moveRight();
            subscriber.awaitEvent();
        }
        for (int i = 0; i < 5; i++) {
            game.moveDown();
            subscriber.awaitEvent();
        }

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        GameControls.ValidMoves validMoves = event.validMoves();

        // All moves should be valid at center
        assertTrue(validMoves.moves().get(GameControls.Move.UP));
        assertTrue(validMoves.moves().get(GameControls.Move.DOWN));
        assertTrue(validMoves.moves().get(GameControls.Move.LEFT));
        assertTrue(validMoves.moves().get(GameControls.Move.RIGHT));
    }

    // ==================== Move Enum Tests ====================

    @Test
    @DisplayName("move(Move.UP) should call moveUp()")
    void testMoveEnumUp() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Consume NewGameEvent

        game.moveDown(); // Move to (0,1) so UP is valid
        subscriber.awaitEvent();

        game.move(GameControls.Move.UP);
        subscriber.awaitEvent();

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.UP, event.direction());
    }

    @Test
    @DisplayName("move(Move.DOWN) should call moveDown()")
    void testMoveEnumDown() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent();

        game.move(GameControls.Move.DOWN);
        subscriber.awaitEvent();

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.DOWN, event.direction());
    }

    @Test
    @DisplayName("move(Move.LEFT) should call moveLeft()")
    void testMoveEnumLeft() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent();

        game.moveRight(); // Move to (1,0) so LEFT is valid
        subscriber.awaitEvent();

        game.move(GameControls.Move.LEFT);
        subscriber.awaitEvent();

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.LEFT, event.direction());
    }

    @Test
    @DisplayName("move(Move.RIGHT) should call moveRight()")
    void testMoveEnumRight() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent();

        game.move(GameControls.Move.RIGHT);
        subscriber.awaitEvent();

        GameEvent.MoveEvent event = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(GameControls.Move.RIGHT, event.direction());
    }

    // ==================== Flow API Tests ====================

    @Test
    @DisplayName("Multiple subscribers should all receive events")
    void testMultipleSubscribers() throws InterruptedException {
        TestSubscriber subscriber1 = new TestSubscriber();
        TestSubscriber subscriber2 = new TestSubscriber();
        TestSubscriber subscriber3 = new TestSubscriber();

        game.subscribe(subscriber1);
        game.subscribe(subscriber2);
        game.subscribe(subscriber3);

        game.newGame();

        subscriber1.awaitEvent();
        subscriber2.awaitEvent();
        subscriber3.awaitEvent();

        assertInstanceOf(GameEvent.NewGameEvent.class, subscriber1.getLastEvent());
        assertInstanceOf(GameEvent.NewGameEvent.class, subscriber2.getLastEvent());
        assertInstanceOf(GameEvent.NewGameEvent.class, subscriber3.getLastEvent());
    }

    @Test
    @DisplayName("Subscriber should receive onComplete() when publisher closes")
    void testSubscriberOnComplete() throws InterruptedException {
        game.subscribe(subscriber);
        game.close();

        assertTrue(subscriber.awaitComplete(1, TimeUnit.SECONDS),
                   "Subscriber should receive onComplete within 1 second");
        assertTrue(subscriber.isCompleted(), "Subscriber should be marked as completed");
    }

    @Test
    @DisplayName("Publisher should not accept new subscriptions after close()")
    void testNoSubscriptionsAfterClose() {
        game.close();

        TestSubscriber lateSubscriber = new TestSubscriber();
        game.subscribe(lateSubscriber);

        // Subscriber should receive onComplete immediately or onError
        // (behavior may vary depending on SubmissionPublisher implementation)
        assertTrue(lateSubscriber.isCompleted() || lateSubscriber.hasError(),
                   "Late subscriber should be notified of closed state");
    }

    @Test
    @DisplayName("getPublisher() should return the same publisher instance")
    void testGetPublisher() {
        Flow.Publisher<GameEvent> publisher1 = game.getPublisher();
        Flow.Publisher<GameEvent> publisher2 = game.getPublisher();

        assertSame(publisher1, publisher2, "getPublisher() should return same instance");
    }

    @Test
    @DisplayName("Subscriber can cancel subscription")
    void testSubscriptionCancel() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // Receive NewGameEvent

        subscriber.cancel(); // Cancel subscription

        game.moveRight(); // This event should not be received

        // Wait a bit to ensure event doesn't arrive
        Thread.sleep(100);

        // Subscriber should still only have the NewGameEvent
        assertEquals(1, subscriber.getEventCount(),
                     "Subscriber should not receive events after cancellation");
    }

    // ==================== Sequence Tests ====================

    @Test
    @DisplayName("Complex movement sequence should work correctly")
    void testComplexMovementSequence() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent(); // (0,0)

        game.moveRight(); // (1,0)
        subscriber.awaitEvent();
        game.moveRight(); // (2,0)
        subscriber.awaitEvent();
        game.moveDown(); // (2,1)
        subscriber.awaitEvent();
        game.moveDown(); // (2,2)
        subscriber.awaitEvent();
        game.moveLeft(); // (1,2)
        subscriber.awaitEvent();
        game.moveUp(); // (1,1)
        subscriber.awaitEvent();

        GameEvent.MoveEvent finalMove = (GameEvent.MoveEvent) subscriber.getLastEvent();
        assertEquals(1, finalMove.newLocation().x());
        assertEquals(1, finalMove.newLocation().y());
    }

    @Test
    @DisplayName("newGame() should reset position regardless of current location")
    void testNewGameResetsPosition() throws InterruptedException {
        game.subscribe(subscriber);
        game.newGame();
        subscriber.awaitEvent();

        // Move to some arbitrary location
        for (int i = 0; i < 3; i++) {
            game.moveRight();
            subscriber.awaitEvent();
        }
        for (int i = 0; i < 5; i++) {
            game.moveDown();
            subscriber.awaitEvent();
        }

        // Reset with newGame
        game.newGame();
        subscriber.awaitEvent();

        GameEvent.NewGameEvent event = (GameEvent.NewGameEvent) subscriber.getLastEvent();
        assertEquals(0, event.startingLocation().x());
        assertEquals(0, event.startingLocation().y());
    }

    // ==================== Helper Test Subscriber Class ====================

    /**
     * Test subscriber implementation for capturing and verifying GameEvents.
     */
    private static class TestSubscriber implements Flow.Subscriber<GameEvent> {
        private Flow.Subscription subscription;
        private final List<GameEvent> receivedEvents = new ArrayList<>();
        private final CountDownLatch eventLatch = new CountDownLatch(1);
        private final CountDownLatch completeLatch = new CountDownLatch(1);
        private boolean completed = false;
        private Throwable error = null;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(GameEvent item) {
            receivedEvents.add(item);
            eventLatch.countDown();
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
            completeLatch.countDown();
        }

        @Override
        public void onComplete() {
            completed = true;
            completeLatch.countDown();
        }

        public void awaitEvent() throws InterruptedException {
            assertTrue(eventLatch.await(1, TimeUnit.SECONDS),
                       "Event should arrive within 1 second");
        }

        public boolean awaitComplete(long timeout, TimeUnit unit) throws InterruptedException {
            return completeLatch.await(timeout, unit);
        }

        public GameEvent getLastEvent() {
            assertFalse(receivedEvents.isEmpty(), "No events received");
            return receivedEvents.get(receivedEvents.size() - 1);
        }

        public int getEventCount() {
            return receivedEvents.size();
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean hasError() {
            return error != null;
        }

        public void cancel() {
            if (subscription != null) {
                subscription.cancel();
            }
        }
    }
}
