import java.util.List;
import java.util.NoSuchElementException;

/**
 * Connects the text-based ConsoleView to the LabSession model. The controller
 * owns user-input parsing and decides which model operation should run. It does
 * not store queue data or implement session rules.
 *
 * @author Stefan Fernandez
 * @version November 8, 2026
 */
public class LabController
{
    private final LabSession session;
    private final ConsoleView view;

    /**
     * Creates a controller for one lab session and one console view.
     *
     * @param session
     *            model containing the waiting line and active request
     * @param view
     *            view used for prompts and messages
     */
    public LabController(LabSession session, ConsoleView view)
    {
        this.session = session;
        this.view = view;
    }


    /**
     * Runs the menu until the user selects Exit or input ends.
     */
    public void run()
    {
        boolean running = true;

        while (running)
        {
            view.showMenu();
            String choiceText = view.ask("Choice: ");

            // End-of-file is treated as Exit so scripted tests and redirected
            // input close cleanly instead of throwing an exception.
            if (choiceText == null)
            {
                running = false;
                continue;
            }

            try
            {
                int choice = Integer.parseInt(choiceText.trim());
                switch (choice)
                {
                    case 1:
                        submit(
                            view.ask("Student ID: "),
                            view.ask("Student name: "),
                            view.ask("Seat: "),
                            view.ask("Question: "));
                        break;
                    case 2:
                        cancel(
                            view.ask("Request ID: "),
                            view.ask("Student ID: "));
                        break;
                    case 3:
                        helpNext();
                        break;
                    case 4:
                        completeActive();
                        break;
                    case 5:
                        showQueue();
                        break;
                    case 6:
                        showPosition(view.ask("Request ID: "));
                        break;
                    case 7:
                        showEstimatedWait(view.ask("Request ID: "));
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        view.showMessage(
                            "Please enter a number from the menu.");
                        break;
                }
            }
            catch (NumberFormatException exception)
            {
                // Menu input is an input-format concern, so it is handled here
                // rather than inside LabSession.
                view.showMessage("Please enter a number from the menu.");
            }
        }
    }


    /**
     * Submits a request and reports the result to the user.
     */
    public
        void
        submit(String studentId, String name, String seat, String question)
    {
        try
        {
            HelpRequest request =
                session.submitRequest(studentId, name, seat, question);
            view.showMessage(
                "Request submitted. Your request ID is #"
                    + request.getRequestId() + ".");
            showQueue();
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            // The model supplies the business-rule message; the controller
            // only forwards it to the view.
            view.showMessage(exception.getMessage());
        }
    }


    /**
     * Parses a request ID and attempts to cancel the matching waiting request.
     */
    public void cancel(String requestIdText, String studentId)
    {
        Long requestId = parseRequestId(requestIdText);
        if (requestId == null)
        {
            return;
        }

        if (session.cancelRequest(requestId, studentId))
        {
            view.showMessage("Request #" + requestId + " was cancelled.");
            showQueue();
        }
        else
        {
            view.showMessage(
                "No waiting request matches that ID and student ID.");
        }
    }


    /**
     * Moves the oldest waiting request into the active slot.
     */
    public void helpNext()
    {
        try
        {
            HelpRequest request = session.helpNext();
            view.showMessage(
                "Now helping " + request.getStudentName() + " (request #"
                    + request.getRequestId() + ").");
            showQueue();
        }
        catch (NoSuchElementException exception)
        {
            view.showMessage("No students are waiting.");
        }
        catch (IllegalStateException exception)
        {
            view.showMessage("Complete the current request first.");
        }
    }


    /**
     * Completes and clears the active request.
     */
    public void completeActive()
    {
        try
        {
            HelpRequest request = session.completeActive();
            view.showMessage(
                "Completed request #" + request.getRequestId() + ".");
            showQueue();
        }
        catch (NoSuchElementException exception)
        {
            view.showMessage("There is no active request to complete.");
        }
    }


    /**
     * Sends a fresh model snapshot to the view.
     */
    public void showQueue()
    {
        List<HelpRequest> waiting = session.waitingSnapshot();
        HelpRequest active = session.activeRequest().orElse(null);
        view.showLine(waiting, active);
    }


    /**
     * Displays the waiting position for a request ID.
     */
    public void showPosition(String requestIdText)
    {
        Long requestId = parseRequestId(requestIdText);
        if (requestId == null)
        {
            return;
        }

        int position = session.positionOf(requestId);
        if (position == -1)
        {
            view.showMessage("That request is not waiting.");
        }
        else
        {
            view.showMessage(
                "Request #" + requestId + " is position " + position
                    + " in the waiting line.");
        }
    }


    /**
     * Converts typed request-ID text into a long.
     *
     * @return parsed ID, or null when the text is blank or invalid
     */
    private Long parseRequestId(String requestIdText)
    {
        try
        {
            if (requestIdText == null || requestIdText.trim().isEmpty())
            {
                throw new NumberFormatException();
            }
            return Long.parseLong(requestIdText.trim());
        }
        catch (NumberFormatException exception)
        {
            view.showMessage("Request ID must be a number.");
            return null;
        }
    }


    /**
     * Displays the estimated wait for a waiting request.
     *
     * @param requestIdText
     *            the ID entered by the user
     */
    public void showEstimatedWait(String requestIdText)
    {
        Long requestId = parseRequestId(requestIdText);

        if (requestId == null)
        {
            return;
        }

        long minutes = session.estimatedWaitMinutes(requestId);

        if (minutes < 0)
        {
            view.showMessage("That request is not waiting.");
        }
        else
        {
            view.showEstimatedWait(requestId, minutes);
        }
    }
}
