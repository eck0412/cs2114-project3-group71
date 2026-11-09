import java.util.Scanner;

/**
 * Starts the TAline application. Creates the model, view, and controller, then
 * opens the menu.
 * 
 * @author erikkwon
 * @version Nov 8, 2026
 */

/**
 * Starts one local lab session.
 *
 * @param args
 *            command-line arguments; not used
 */
public final class TAline
{
    public static void main(String[] args)
    {
        LabSession session = new LabSession();
        ConsoleView view = new ConsoleView(new Scanner(System.in), System.out);
        new LabController(session, view).run();
    };;
}
