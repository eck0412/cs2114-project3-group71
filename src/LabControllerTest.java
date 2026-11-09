import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests the controller's connection between the ConsoleView and LabSession.
 *
 * These tests use a fake input stream and a captured output stream, so they do
 * not require a real keyboard or terminal. The tests focus on controller
 * behavior such as parsing input, calling the model, and displaying messages.
 * Validation rules that belong entirely to LabSession should be tested in the
 * LabSession test class.
 *
 * @author Stefan Fernandez
 * @version November 8, 2026
 */
public class LabControllerTest extends TestCase {
    // ~ Fields ................................................................
    /** Captures text printed by the ConsoleView. */
    private ByteArrayOutputStream outputBytes;

    /** Print stream connected to the fake output. */
    private PrintStream output;

    /** Model used by the controller during each test. */
    private LabSession session;

    /** View connected to fake input and output. */
    private ConsoleView view;

    /** Controller being tested. */
    private LabController controller;

    // ~ Constructors ..........................................................
    // TestCase creates this test class.

    // ~ Public Methods ........................................................
    /**
     * Creates a new model, view, and controller before each test.
     */
    public void setUp() {
        outputBytes = new ByteArrayOutputStream();
        output = new PrintStream(outputBytes);
        session = new LabSession();
        view = new ConsoleView(new Scanner(""), output);
        controller = new LabController(session, view);
    }


    /**
     * Submitting valid information calls the model and displays a confirmation.
     */
    public void testSubmitValidRequest() {
        controller.submit("S1", "Alice", "A1", "Need help");

        String text = outputBytes.toString();
        assertTrue(text.contains("Request submitted."));
        assertTrue(text.contains("Alice"));
        assertEquals(1, session.waitingSnapshot().size());
        assertEquals("Alice", session.waitingSnapshot().get(0)
            .getStudentName());
    }


    /**
     * A model validation error is forwarded to the view instead of crashing.
     */
    public void testSubmitInvalidRequest() {
        controller.submit("S1", "", "A1", "Need help");

        String text = outputBytes.toString();
        assertTrue(text.contains("Student name is required"));
        assertTrue(session.waitingSnapshot().isEmpty());
    }


    /**
     * A valid request ID and matching student ID cancel a waiting request.
     */
    public void testCancelValidRequest() {
        HelpRequest request = session.submitRequest("S1", "Alice", "A1",
            "Need help");
        outputBytes.reset();

        controller.cancel(Long.toString(request.getRequestId()), "S1");

        assertTrue(outputBytes.toString().contains("was cancelled"));
        assertTrue(session.waitingSnapshot().isEmpty());
    }


    /**
     * Non-numeric request IDs are rejected by the controller before the model
     * is called.
     */
    public void testCancelWithNonNumericRequestId() {
        controller.cancel("abc", "S1");

        assertTrue(outputBytes.toString().contains(
            "Request ID must be a number."));
        assertTrue(session.waitingSnapshot().isEmpty());
    }


    /**
     * Cancellation fails when the student ID does not own the request.
     */
    public void testCancelWithWrongStudent() {
        HelpRequest request = session.submitRequest("S1", "Alice", "A1",
            "Need help");
        outputBytes.reset();

        controller.cancel(Long.toString(request.getRequestId()), "S2");

        assertTrue(outputBytes.toString().contains(
            "No waiting request matches that ID and student ID."));
        assertEquals(1, session.waitingSnapshot().size());
    }


    /**
     * Helping the next student moves the oldest request into the active slot.
     */
    public void testHelpNext() {
        session.submitRequest("S1", "Alice", "A1", "Question 1");
        outputBytes.reset();

        controller.helpNext();

        String text = outputBytes.toString();
        assertTrue(text.contains("Now helping Alice"));
        assertTrue(text.contains("Currently helping: Alice"));
        assertTrue(session.activeRequest().isPresent());
        assertEquals("Alice", session.activeRequest().get().getStudentName());
    }


    /**
     * Helping an empty waiting line displays the expected user-friendly error.
     */
    public void testHelpNextWhenEmpty() {
        controller.helpNext();

        assertTrue(outputBytes.toString().contains("No students are waiting."));
    }


    /**
     * Calling helpNext twice without completing the active request is rejected.
     */
    public void testHelpNextWhileRequestIsActive() {
        session.submitRequest("S1", "Alice", "A1", "Question 1");
        controller.helpNext();
        outputBytes.reset();

        controller.helpNext();

        assertTrue(outputBytes.toString().contains(
            "Complete the current request first."));
    }


    /**
     * Completing an active request clears the active slot.
     */
    public void testCompleteActive() {
        session.submitRequest("S1", "Alice", "A1", "Question 1");
        controller.helpNext();
        outputBytes.reset();

        controller.completeActive();

        assertTrue(outputBytes.toString().contains("Completed request"));
        assertFalse(session.activeRequest().isPresent());
    }


    /**
     * Completing with no active request displays an error and leaves the model
     * unchanged.
     */
    public void testCompleteWhenNothingIsActive() {
        controller.completeActive();

        assertTrue(outputBytes.toString().contains(
            "There is no active request to complete."));
    }


    /**
     * The controller reports the position of a waiting request.
     */
    public void testShowPosition() {
        HelpRequest request = session.submitRequest("S1", "Alice", "A1",
            "Need help");
        outputBytes.reset();

        controller.showPosition(Long.toString(request.getRequestId()));

        assertTrue(outputBytes.toString().contains("position 1"));
    }


    /**
     * A request that is no longer waiting has no displayed position.
     */
    public void testShowPositionForUnknownRequest() {
        controller.showPosition("999");

        assertTrue(outputBytes.toString().contains(
            "That request is not waiting."));
    }


    /**
     * showQueue sends the current model snapshot to the view.
     */
    public void testShowQueue() {
        session.submitRequest("S1", "Alice", "A1", "Question 1");
        session.submitRequest("S2", "Bob", "B2", "Question 2");

        controller.showQueue();

        String text = outputBytes.toString();
        assertTrue(text.contains("Waiting line:"));
        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("Bob"));
        assertTrue(text.indexOf("Alice") < text.indexOf("Bob"));
    }


    /**
     * The menu loop handles a valid action and exits when the user enters 0.
     */
    public void testRunSubmitThenExit() {
        String input = "1\nS1\nAlice\nA1\nNeed help\n0\n";
        view = new ConsoleView(new Scanner(input), output);
        controller = new LabController(session, view);

        controller.run();

        String text = outputBytes.toString();
        assertTrue(text.contains("=== TAline ==="));
        assertTrue(text.contains("Request submitted."));
        assertEquals(1, session.waitingSnapshot().size());
    }


    /**
     * Invalid menu text causes an error message and the loop continues.
     */
    public void testRunWithInvalidMenuInput() {
        String input = "abc\n0\n";
        view = new ConsoleView(new Scanner(input), output);
        controller = new LabController(session, view);

        controller.run();

        String text = outputBytes.toString();
        assertTrue(text.contains("Please enter a number from the menu."));
        assertTrue(text.contains("=== TAline ==="));
    }


    /**
     * End-of-input exits the menu cleanly rather than throwing an exception.
     */
    public void testRunWhenInputEnds() {
        view = new ConsoleView(new Scanner(""), output);
        controller = new LabController(session, view);

        controller.run();

        // Reaching this assertion means run returned without crashing.
        assertTrue(outputBytes.toString().contains("Choice: "));
    }
}
