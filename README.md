# cs2114-project3-group71

Reflection

We kept the specification’s model, view, and controller split. HelpRequest, LinkedQueue, and LabSession hold request data and rules; ConsoleView handles input and output; and LabController connects them. Validation remains in LabSession, with menu and request-ID parsing in the controller. The model does not depend on the view and can be tested on its own.

We worked out the linked FIFO queue and how to display requests without putting queue logic in the view. Separate-device access, multiple TAs, wait estimates, and saving the line remain open. We still need LabSessionTest.java and a full check of the combined MVP, including invalid inputs and empty-queue actions.

Next Steps

First, combine the team’s classes and verify submit, cancel, position, help next, and complete. Then add LabSessionTest.java, finish bad-input tests, and fix any failures.

For Deliverable 4, we plan to add skip and requeue, which moves the active request to the back of the line. We predict changes to LabSession, LabController, ConsoleView, and their tests, but not HelpRequest or LinkedQueue. New tests will cover skipping with other students waiting, skipping the only request, and trying to skip when none is active.

Tue will review request tests; Vibha will handle queue and session logic and tests; Erik will update the view and its tests; and Stefan will handle controller changes and integration. We are deferring the other stretch goals.


## Attribution

| Class or test file | Team member | Contribution |
| --- | --- | --- |
| `HelpRequest.java` | Tue | Implemented the request data class. |
| `HelpRequestTest.java` | Tue | Wrote tests for request fields and getters. |
| `LinkedQueue.java` | Vibha | Implemented the linked FIFO queue. |
| `LinkedQueueTest.java` | To confirm | Wrote queue tests. |
| `LabSession.java` | Vibha | Implemented session logic. |
| `ConsoleView.java` | Erik | Implemented the menu, prompts, messages, and waiting line display. |
| `ConsoleViewTest.java` | Erik | Wrote tests for console input and displayed output. |
| `LabController.java` | Stefan | Connected the view to the model and handled menu actions. |
| `LabControllerTest.java` | Stefan | Wrote tests for controller actions and input handling. |
| `TAline.java` | Erik | Added the application entry point. |

**GenAI note:** GenAI helped draft and review parts of the TALine code, tests, and documentation. Team members reviewed the suggestions and are responsible for checking their own files.
