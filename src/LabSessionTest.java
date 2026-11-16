import java.util.List;
import java.util.NoSuchElementException;
import student.TestCase;

/**
 * Tests labsession independently of the view and controller
 * 
 *  @author erikkwon
 *  @version Nov 14, 2026
 */
public class LabSessionTest extends TestCase
{
    private LabSession session;

    /**
     * Creates a fresh session before each test.
     */
    public void setUp()
    {
        session = new LabSession();
    }


    /**
     * Tests that a valid request is added and its fields are trimmed.
     */
    public void testSubmitRequest()
    {
        HelpRequest request = session.submitRequest(
            " S1 ", " Alice ", " A1 ", " Help ");

        assertEquals(1L, request.getRequestId());
        assertEquals("S1", request.getStudentId());
        assertEquals("Alice", request.getStudentName());
        assertEquals("A1", request.getSeat());
        assertEquals("Help", request.getQuestion());
        assertEquals(1, session.waitingSnapshot().size());
    }


    /**
     * Tests that blank fields and duplicate student IDs are rejected.
     */
    public void testSubmitRejectsSpacesAndDuplicate()
    {
        try
        {
            session.submitRequest("S1", "   ", "A1", "Help");
            fail("A spaces-only name must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }

        session.submitRequest("S1", "Alice", "A1", "Help");

        try
        {
            session.submitRequest("s1", "Other Name", "B1", "Help");
            fail("A duplicate student ID must be rejected");
        }
        catch (IllegalStateException expected)
        {
            assertEquals(1, session.waitingSnapshot().size());
        }
    }


    /**
     * Tests that a question over the allowed length is rejected.
     */
    public void testSubmitRejectsLongQuestion()
    {
        try
        {
            session.submitRequest(
                "S1", "Alice", "A1", "x".repeat(301));
            fail("A question over 300 characters must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }
    }


    /**
     * Tests cancellation by the owner and rejection of incorrect IDs.
     */
    public void testCancelRequest()
    {
        HelpRequest request = session.submitRequest(
            "S1", "Alice", "A1", "Help");

        assertFalse(session.cancelRequest(
            request.getRequestId(), "wrong-student"));
        assertEquals(1, session.waitingSnapshot().size());

        assertTrue(session.cancelRequest(
            request.getRequestId(), "S1"));
        assertTrue(session.waitingSnapshot().isEmpty());

        assertFalse(session.cancelRequest(999, "S1"));
    }


    /**
     * Tests serving the first request and errors for invalid serve actions.
     */
    public void testHelpNext()
    {
        try
        {
            session.helpNext();
            fail("An empty line must be rejected");
        }
        catch (NoSuchElementException expected)
        {
            assertFalse(session.activeRequest().isPresent());
        }

        HelpRequest alice = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        HelpRequest bob = session.submitRequest(
            "S2", "Bob", "B1", "Help");

        assertEquals(alice.getRequestId(),
            session.helpNext().getRequestId());
        assertEquals(alice.getRequestId(),
            session.activeRequest().get().getRequestId());
        assertEquals(bob.getRequestId(),
            session.waitingSnapshot().get(0).getRequestId());

        try
        {
            session.helpNext();
            fail("A second request cannot become active yet");
        }
        catch (IllegalStateException expected)
        {
            assertEquals(1, session.waitingSnapshot().size());
        }
    }


    /**
     * Tests completing an active request and rejecting completion when empty.
     */
    public void testCompleteActive()
    {
        try
        {
            session.completeActive();
            fail("Nothing is active");
        }
        catch (NoSuchElementException expected)
        {
            assertFalse(session.activeRequest().isPresent());
        }

        HelpRequest request = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        session.helpNext();

        assertEquals(request.getRequestId(),
            session.completeActive().getRequestId());
        assertFalse(session.activeRequest().isPresent());
    }


    /**
     * Tests waiting positions and IDs that are no longer waiting.
     */
    public void testPositionOf()
    {
        HelpRequest alice = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        HelpRequest bob = session.submitRequest(
            "S2", "Bob", "B1", "Help");

        assertEquals(1, session.positionOf(alice.getRequestId()));
        assertEquals(2, session.positionOf(bob.getRequestId()));
        assertEquals(-1, session.positionOf(999));

        session.helpNext();

        assertEquals(-1, session.positionOf(alice.getRequestId()));
        assertEquals(1, session.positionOf(bob.getRequestId()));
    }


    /**
     * Tests that a waiting snapshot cannot be changed and stays independent.
     */
    public void testWaitingSnapshot()
    {
        HelpRequest alice = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        List<HelpRequest> snapshot = session.waitingSnapshot();

        session.submitRequest("S2", "Bob", "B1", "Help");

        assertEquals(1, snapshot.size());
        assertEquals(alice.getRequestId(),
            snapshot.get(0).getRequestId());

        try
        {
            snapshot.clear();
            fail("The snapshot must not be modifiable");
        }
        catch (UnsupportedOperationException expected)
        {
            assertEquals(2, session.waitingSnapshot().size());
        }
    }


    /**
     * Tests the active request before serving, during service, and afterward.
     */
    public void testActiveRequest()
    {
        assertFalse(session.activeRequest().isPresent());

        HelpRequest request = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        session.helpNext();

        assertTrue(session.activeRequest().isPresent());
        assertEquals(request.getRequestId(),
            session.activeRequest().get().getRequestId());

        session.completeActive();
        assertFalse(session.activeRequest().isPresent());
    }


    /**
     * Tests estimated waits for waiting, active, and unknown requests.
     */
    public void testEstimatedWaitMinutes()
    {
        HelpRequest alice = session.submitRequest(
            "S1", "Alice", "A1", "Help");
        HelpRequest bob = session.submitRequest(
            "S2", "Bob", "B1", "Help");

        assertEquals(0L,
            session.estimatedWaitMinutes(alice.getRequestId()));
        assertEquals(10L,
            session.estimatedWaitMinutes(bob.getRequestId()));
        assertEquals(-1L,
            session.estimatedWaitMinutes(999));

        session.helpNext();

        assertEquals(-1L,
            session.estimatedWaitMinutes(alice.getRequestId()));
        assertEquals(10L,
            session.estimatedWaitMinutes(bob.getRequestId()));
    }
    /**
     * Tests for null inputs
     */
    public void testSubmitRejectsNullInputs()
    {
        try
        {
            session.submitRequest(null, "Alice", "A1", "Help");
            fail("Null student ID must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }

        try
        {
            session.submitRequest("S1", null, "A1", "Help");
            fail("Null student name must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }

        try
        {
            session.submitRequest("S1", "Alice", null, "Help");
            fail("Null seat must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }

        try
        {
            session.submitRequest("S1", "Alice", "A1", null);
            fail("Null question must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(session.waitingSnapshot().isEmpty());
        }
    }
}