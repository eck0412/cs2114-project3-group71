/**
 * One student's request for help in the lab.
 * Immutable: every field is set once in the constructor and never changes.
 * Validation is done by LabSession, not here.
 */
public class HelpRequest {
    private long requestId;
    private String studentId;
    private String studentName;
    private String seat;
    private String question;

    /**
     * Creates a help request. LabSession passes in already-validated,
     * trimmed values and a unique requestId.
     *
     * @param requestId   unique id assigned by LabSession
     * @param studentId   the student's id
     * @param studentName the student's name
     * @param seat        where the student is sitting
     * @param question    what the student needs help with
     */
    public HelpRequest(long requestId, String studentId,
            String studentName, String seat, String question) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.seat = seat;
        this.question = question;
    }

    /**
     * @return the unique request id
     */
    public long getRequestId() {
        return requestId;
    }

    /**
     * @return the student's id
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * @return the student's name
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * @return the student's seat
     */
    public String getSeat() {
        return seat;
    }

    /**
     * @return the student's question
     */
    public String getQuestion() {
        return question;
    }
}
