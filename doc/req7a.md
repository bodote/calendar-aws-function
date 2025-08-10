# requirement 7a
as a developer i want an end to end test for my project that at least tests the happy path creating a new scheduled event.

## acceptance criteria
use `WoodleApplicationTests` to spin up the application and then use playwright to go to  `http://localhost:8080/schedule-event` and input some test strings into the input fields, then click "next" button to go to the next page and so on until reaching `http://localhost:8080/event` and check if all data from previous input fields are there.

## remark
since this test will be implemented after the production code is already there, the TDD rules do not apply here.