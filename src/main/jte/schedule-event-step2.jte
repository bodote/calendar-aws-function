@param java.util.Map<String, String> pollData = null
@param String uuid

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Event - Step 2 - Woodle</title>
    <style>
        body {
            margin: 0;
            padding: 20px;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            font-family: Arial, sans-serif;
        }
        .container {
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        header {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 20px;
            justify-content: center;
        }
        header img {
            height: 50px;
            width: auto;
        }
        form {
            text-align: left;
            margin-top: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="date"], input[type="time"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .time-slots {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 10px;
        }
        .back-button {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
            text-decoration: none;
            display: inline-block;
        }
        .back-button:hover {
            background-color: #545b62;
        }
        .next-button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .next-button:hover {
            background-color: #0056b3;
        }
        .navigation {
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <img src="/woodle-logo.jpeg" alt="Woodle Logo" data-test-woodle-logo>
            <h1>Schedule Event - Step 2</h1>
        </header>
        <main>
            @if(pollData != null)
                <p>Activity: <strong>${pollData.get("activityTitle")}</strong></p>
                <p>Organizer: <strong>${pollData.get("name")}</strong></p>
            @endif
            
            <form data-test-schedule-event-step2-form method="post" action="/schedule-event-step2/${uuid}">
                <div class="form-group">
                    <label for="eventDate">Select Date:</label>
                    <input type="date" id="eventDate" name="eventDate" data-test-date-field required>
                </div>
                
                <div class="form-group">
                    <label>Available Time Slots:</label>
                    <div class="time-slots">
                        <input type="time" name="timeSlot1" data-test-time-field>
                        <input type="time" name="timeSlot2" data-test-time-field>
                        <input type="time" name="timeSlot3" data-test-time-field>
                        <input type="time" name="timeSlot4" data-test-time-field>
                    </div>
                </div>
                
                <div class="navigation">
                    <a href="/schedule-event/${uuid}" class="back-button" data-test-back-button>Back</a>
                    <button type="submit" class="next-button" data-test-next-button>Next</button>
                </div>
            </form>
        </main>
    </div>
</body>
</html> 