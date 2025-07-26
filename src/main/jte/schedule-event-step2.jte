@param java.util.Map<String, String> pollData = null
@param String uuid
@param int proposalCount = 1

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
        .add-proposal-button {
            background-color: #28a745;
            color: white;
            padding: 8px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .add-proposal-button:hover {
            background-color: #218838;
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
                @for(int i = 1; i <= proposalCount; i++)
                    <div class="form-group">
                        @if(i == 1)
                            <label for="eventDate">Select Date:</label>
                            <input type="date" 
                                   id="eventDate" 
                                   name="eventDate" 
                                   data-test="date-field"
                                   value="${pollData != null && pollData.get("eventDate") != null ? pollData.get("eventDate") : ""}"
                                   required>
                        @else
                            <label for="eventDate${i}">Alternative Date ${i}:</label>
                            <input type="date" 
                                   id="eventDate${i}" 
                                   name="eventDate${i}" 
                                   data-test="date-field-${i}"
                                   value="${pollData != null && pollData.get("eventDate" + i) != null ? pollData.get("eventDate" + i) : ""}">
                        @endif
                    </div>
                    
                    <div class="form-group">
                        @if(i == 1)
                            <label>Available Time Slots:</label>
                        @else
                            <label>Time Slots for Alternative ${i}:</label>
                        @endif
                        <div class="time-slots">
                            @for(int j = 1; j <= 3; j++)
                                @if(i == 1)
                                    <input type="time" 
                                           name="timeSlot${j}" 
                                           data-test="time-field${j}"
                                           value="${pollData != null && pollData.get("timeSlot" + j) != null ? pollData.get("timeSlot" + j) : ""}">
                                @else
                                    <input type="time" 
                                           name="timeSlot${i}_${j}" 
                                           data-test="time-field-${i}-${j}"
                                           value="${pollData != null && pollData.get("timeSlot" + i + "_" + j) != null ? pollData.get("timeSlot" + i + "_" + j) : ""}">
                                @endif
                            @endfor
                        </div>
                    </div>
                @endfor
                
                <button type="submit" name="action" value="add-proposal" class="add-proposal-button" data-test="add-proposal-button">
                    <img src="/Plus-Symbol-Transparent-small.png" alt="+" style="height: 20px; width: 20px;">
                </button>
                
                <div class="navigation">
                    <button type="submit" name="action" value="back" class="back-button" data-test-back-button>Back</button>
                    <button type="submit" name="action" value="next" class="next-button" data-test-next-button>Next</button>
                </div>
            </form>
        </main>
    </div>
</body>
</html> 