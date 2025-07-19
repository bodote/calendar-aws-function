@param java.util.Map<String, String> pollData = null
@param String uuid = null

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Event - Woodle</title>
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
        input[type="text"], input[type="email"], textarea {
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
        .next-button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
        }
        .next-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <img src="/woodle-logo.jpeg" alt="Woodle Logo" data-test-woodle-logo>
            <h1>Schedule Event</h1>
        </header>
        <main>
            <form data-test-schedule-event-form method="post" action="@if(uuid != null)/schedule-event/${uuid}@else/schedule-event@endif">
                <div class="form-group">
                    <label for="yourName">Your name:</label>
                    <input type="text" id="yourName" name="yourName" data-test-your-name-field 
                           value="${pollData != null && pollData.get("name") != null ? pollData.get("name") : ""}" required>
                </div>
                <div class="form-group">
                    <label for="emailAddress">Email address:</label>
                    <input type="email" id="emailAddress" name="emailAddress" data-test-email-field 
                           value="${pollData != null && pollData.get("email") != null ? pollData.get("email") : ""}" required>
                </div>
                <div class="form-group">
                    <label for="activityTitle">Activity title:</label>
                    <input type="text" id="activityTitle" name="activityTitle" data-test-activity-title-field 
                           value="${pollData != null && pollData.get("activityTitle") != null ? pollData.get("activityTitle") : ""}" required>
                </div>
                <div class="form-group">
                    <label for="description">Description:</label>
                    <textarea id="description" name="description" data-test-description-field rows="4" required>${pollData != null && pollData.get("description") != null ? pollData.get("description") : ""}</textarea>
                </div>
                <div class="form-group">
                    <button type="submit" class="next-button" data-test-next-button>Next</button>
                </div>
            </form>
        </main>
    </div>
</body>
</html> 