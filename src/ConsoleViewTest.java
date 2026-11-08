import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests the text displayed and input read by ConsoleView.
 *
 * 
 *  @author erikkwon
 *  @version Nov 8, 2026
 */
public class ConsoleViewTest extends TestCase
{
    //~ Fields ................................................................
    private ByteArrayOutputStream outputBytes;
    private PrintStream output;
    private ConsoleView view;


    //~ Constructors ..........................................................
    // TestCase creates this test class.


    //~ Public Methods ........................................................

    /**
     * Creates a fresh view before each test.
     */
    public void setUp()
    {
        outputBytes = new ByteArrayOutputStream();
        output = new PrintStream(outputBytes);
        view = new ConsoleView(new Scanner(""), output);
    }


    /**
     * The menu includes the main actions and exit.
     */
    public void testShowMenu()
    {
        view.showMenu();

        String text = outputBytes.toString();
        assertTrue(text.contains("=== TAline ==="));
        assertTrue(text.contains("1. Submit a help request"));
        assertTrue(text.contains("0. Exit"));
    }


    /**
     * A prompt displays its text and returns the entered line.
     */
    public void testAsk()
    {
        view = new ConsoleView(new Scanner("Alice\n"), output);

        assertEquals("Alice", view.ask("Name: "));
        assertTrue(outputBytes.toString().contains("Name: "));
    }


    /**
     * Ended input returns null instead of crashing.
     */
    public void testAskWhenInputEnds()
    {
        assertNull(view.ask("Choice: "));
    }


    /**
     * Messages from the controller are displayed.
     */
    public void testShowMessage()
    {
        view.showMessage("No students are waiting.");

        assertTrue(
            outputBytes.toString().contains("No students are waiting."));
    }


    /**
     * An empty line and no active request are displayed clearly.
     */
    public void testShowEmptyLine()
    {
        view.showLine(Collections.<HelpRequest>emptyList(), null);

        String text = outputBytes.toString();
        assertTrue(text.contains("Currently helping: nobody"));
        assertTrue(text.contains("Waiting line is empty."));
    }


    /**
     * The active request and waiting positions appear in order.
     */
    public void testShowLineWithRequests()
    {
        HelpRequest active =
            new HelpRequest(1, "S1", "Alice", "A1", "Question 1");
        HelpRequest first =
            new HelpRequest(2, "S2", "Bob", "B2", "Question 2");
        HelpRequest second =
            new HelpRequest(3, "S3", "Casey", "C3", "Question 3");

        view.showLine(Arrays.asList(first, second), active);

        String text = outputBytes.toString();
        assertTrue(text.contains("Currently helping: Alice"));
        assertTrue(text.contains("1. Bob"));
        assertTrue(text.contains("2. Casey"));
        assertTrue(text.indexOf("1. Bob") < text.indexOf("2. Casey"));
    }
}