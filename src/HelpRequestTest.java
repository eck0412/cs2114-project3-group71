import student.TestCase;

/**
 * Tests for HelpRequest.
 */
public class HelpRequestTest extends TestCase {

    /**
     * Getters return exactly what the constructor was given.
     */
    public void testGetters() {
        HelpRequest r = new HelpRequest(1, "S123", "Bop", "A4", "Why NPE?");
        assertEquals(1, r.getRequestId());
        assertEquals("S123", r.getStudentId());
        assertEquals("Bop", r.getStudentName());
        assertEquals("A4", r.getSeat());
        assertEquals("Why NPE?", r.getQuestion());
    }

    /**
     * Two requests keep their own values
     */
    public void testTwoRequestsAreIndependent() {
        HelpRequest a = new HelpRequest(1, "S1", "Ann", "B1", "Q1");
        HelpRequest b = new HelpRequest(2, "S2", "Ben", "B2", "Q2");
        assertEquals(1, a.getRequestId());
        assertEquals(2, b.getRequestId());
        assertEquals("Ann", a.getStudentName());
        assertEquals("Ben", b.getStudentName());
    }
}
