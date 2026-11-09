import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

/**
 * Manages the lab session state, including the waiting queue, the active
 * request, request validation, duplicate checks, and queue operations.
 *
 * @author Vibha
 * @version November 8, 2026
 */
public class LabSession
{
    private LinkedQueue<HelpRequest> waiting;
    private HelpRequest activeRequest;
    private long nextRequestId;

    /**
     * Creates a new lab session with an empty waiting line and no active
     * request.
     */
    public LabSession()
    {
        this.waiting = new LinkedQueue<>();
        this.activeRequest = null;
        this.nextRequestId = 1L;
    }


    /**
     * Validates and adds a new help request to the waiting line.
     *
     * @param studentId
     *            the student's ID
     * @param studentName
     *            the student's name
     * @param seat
     *            the student's seat
     * @param question
     *            the student's question
     * @return the created HelpRequest
     * @throws IllegalArgumentException
     *             if any field is null, blank, or exceeds length limits
     * @throws IllegalStateException
     *             if the student already has an open waiting or active request
     */
    public HelpRequest submitRequest(
        String studentId,
        String studentName,
        String seat,
        String question)
    {

        // Trim inputs
        String trimmedId = studentId == null ? null : studentId.trim();
        String trimmedName = studentName == null ? null : studentName.trim();
        String trimmedSeat = seat == null ? null : seat.trim();
        String trimmedQuestion = question == null ? null : question.trim();

        // Null or blank validation
        if (trimmedId == null || trimmedId.isEmpty())
        {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (trimmedName == null || trimmedName.isEmpty())
        {
            throw new IllegalArgumentException("Student name is required.");
        }
        if (trimmedSeat == null || trimmedSeat.isEmpty())
        {
            throw new IllegalArgumentException("Seat is required.");
        }
        if (trimmedQuestion == null || trimmedQuestion.isEmpty())
        {
            throw new IllegalArgumentException("Question is required.");
        }

        // Length validation
        if (trimmedId.length() > 20)
        {
            throw new IllegalArgumentException(
                "Student ID must be 20 characters or fewer.");
        }
        if (trimmedName.length() > 50)
        {
            throw new IllegalArgumentException(
                "Student name must be 50 characters or fewer.");
        }
        if (trimmedSeat.length() > 10)
        {
            throw new IllegalArgumentException(
                "Seat must be 10 characters or fewer.");
        }
        if (trimmedQuestion.length() > 300)
        {
            throw new IllegalArgumentException(
                "Question must be 300 characters or fewer.");
        }

        // Duplicate check against active request
        if (activeRequest != null
            && activeRequest.getStudentId().equalsIgnoreCase(trimmedId))
        {
            throw new IllegalStateException(
                "This student already has an open request.");
        }

        // Duplicate check against waiting queue
        for (HelpRequest req : waiting.snapshot())
        {
            if (req.getStudentId().equalsIgnoreCase(trimmedId))
            {
                throw new IllegalStateException(
                    "This student already has an open request.");
            }
        }

        HelpRequest request = new HelpRequest(
            nextRequestId++,
            trimmedId,
            trimmedName,
            trimmedSeat,
            trimmedQuestion);
        waiting.enqueue(request);
        return request;
    }


    /**
     * Removes a student's matching waiting request. Active requests cannot be
     * cancelled.
     *
     * @param requestId
     *            the ID of the request to cancel
     * @param studentId
     *            the student ID owning the request
     * @return true if successfully removed, false otherwise
     */
    public boolean cancelRequest(long requestId, String studentId)
    {
        if (studentId == null)
        {
            return false;
        }
        return waiting.removeFirst(
            req -> req.getRequestId() == requestId
                && req.getStudentId().equalsIgnoreCase(studentId.trim()));
    }


    /**
     * Moves the oldest waiting request into the active slot.
     *
     * @return the newly active HelpRequest
     * @throws NoSuchElementException
     *             if nobody is waiting
     * @throws IllegalStateException
     *             if a request is already active
     */
    public HelpRequest helpNext()
    {
        if (activeRequest != null)
        {
            throw new IllegalStateException(
                "Complete the current request first.");
        }
        if (waiting.isEmpty())
        {
            throw new NoSuchElementException("No students are waiting.");
        }
        activeRequest = waiting.dequeue();
        return activeRequest;
    }


    /**
     * Clears and returns the active request.
     *
     * @return the completed HelpRequest
     * @throws NoSuchElementException
     *             if there is no active request
     */
    public HelpRequest completeActive()
    {
        if (activeRequest == null)
        {
            throw new NoSuchElementException(
                "There is no active request to complete.");
        }
        HelpRequest completed = activeRequest;
        activeRequest = null;
        return completed;
    }


    /**
     * Returns a waiting request's 1-based position, or -1 if not waiting.
     *
     * @param requestId
     *            the request ID to check
     * @return position starting at 1, or -1
     */
    public int positionOf(long requestId)
    {
        List<HelpRequest> snapshot = waiting.snapshot();
        for (int i = 0; i < snapshot.size(); i++)
        {
            if (snapshot.get(i).getRequestId() == requestId)
            {
                return i + 1;
            }
        }
        return -1;
    }


    /**
     * Returns an immutable copy of the waiting requests in queue order.
     *
     * @return immutable list of waiting requests
     */
    public List<HelpRequest> waitingSnapshot()
    {
        return waiting.snapshot();
    }


    /**
     * Returns an Optional containing the active request, if one exists.
     *
     * @return Optional holding the active request or empty
     */
    public Optional<HelpRequest> activeRequest()
    {
        return Optional.ofNullable(activeRequest);
    }
}
