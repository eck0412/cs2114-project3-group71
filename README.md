# cs2114-project3-group71

Reflection

We added estimated wait time as our non-trivial feature instead of the skip/requeue action named in our D3 plan. We made this change because wait time could be built from the queue position and active request already tracked by LabSession. The feature touched LabSession, LabController, ConsoleView, and their tests—the same layers we predicted for skip/requeue. HelpRequest and LinkedQueue did not need to change.

The model calculates the estimate; the view only displays it, so the model/view boundary still holds. The estimate assumes 10 minutes per request ahead and does not measure actual help time. Skip/requeue has not been added yet. We still need to confirm passing normal and bad-input tests for every core model and queue method in the combined project.

Next Steps

First, finish the required model tests and verify blank fields, invalid IDs, and empty-line actions. Then add and test skip/requeue if the team has time. For our three-minute demo, we will submit two requests, show their positions and estimated waits, call and complete the first student, and show how the second student’s estimate changes. We will also demonstrate a bad-input case and the passing model tests. If skip/requeue is completed, we will include it in the demo.

We are keeping estimated wait time and attempting skip/requeue. We are deferring separate-device access, multiple-TA coordination, and saving/restoring the line. Tue will review request tests; Vibha will finish model logic and tests; Erik will verify the view and its tests; and Stefan will handle controller integration and the demo.


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
