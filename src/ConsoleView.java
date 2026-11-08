import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles text input and output for TAline. Does not change the queue or
 * session state.
 * 
 * @author erikkwon
 * @version Nov 7, 2026
 */
public class ConsoleView
{
    // ~ Fields ................................................................
    private Scanner input;
    private PrintStream output;

    // ~ Constructors ..........................................................
    /**
     * Creates a console view.
     *
     * @param inputSource
     *            where user input comes from
     * @param outputStream
     *            where messages are displayed
     */
    public ConsoleView(Scanner inputSource, PrintStream outputStream)
    {
        input = inputSource;
        output = outputStream;
    }

    // ~ Public Methods ........................................................


    /**
     * Displays the main menu.
     */
    public void showMenu()
    {
        output.println();
        output.println("=== TAline ===");
        output.println("1. Submit a help request");
        output.println("2. Cancel a request");
        output.println("3. Help next student");
        output.println("4. Complete current request");
        output.println("5. Show waiting line");
        output.println("6. Check request position");
        output.println("0. Exit");
    }


    /**
     * Displays a prompt and reads one line.
     *
     * @param prompt
     *            text to display
     * @return the entered line, or null if input has ended
     */
    public String ask(String prompt)
    {
        output.print(prompt);

        if (input.hasNextLine())
        {
            return input.nextLine();
        }

        return null;
    }


    /**
     * Displays a message or error from the controller.
     *
     * @param text
     *            message to display
     */
    public void showMessage(String text)
    {
        output.println(text);
    }


    /**
     * Displays the active request and waiting line.
     *
     * @param waiting
     *            requests in first-in, first-out order
     * @param active
     *            the request currently being helped, or null
     */
    public void showLine(List<HelpRequest> waiting, HelpRequest active)
    {
        if (active == null)
        {
            output.println("Currently helping: nobody");
        }
        else
        {
            output.println(
                "Currently helping: " + active.getStudentName() + " (request #"
                    + active.getRequestId() + ")");
        }

        if (waiting.isEmpty())
        {
            output.println("Waiting line is empty.");
            return;
        }

        output.println("Waiting line:");

        for (int i = 0; i < waiting.size(); i++)
        {
            HelpRequest request = waiting.get(i);

            output.println(
                (i + 1) + ". " + request.getStudentName() + " — request #"
                    + request.getRequestId() + ", seat " + request.getSeat());
        }
    }
}
