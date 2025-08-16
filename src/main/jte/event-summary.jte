@param java.util.Map<String, String> pollData
@param String uuid

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Event Summary - Woodle</title>
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
        .event-details {
            text-align: left;
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .detail-group {
            margin-bottom: 15px;
        }
        .detail-label {
            font-weight: bold;
            color: #333;
        }
        .detail-value {
            margin-top: 5px;
            color: #666;
        }
        .shareable-url {
            background-color: #e9ecef;
            padding: 15px;
            border-radius: 8px;
            margin-top: 20px;
        }
        .url-input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background-color: white;
            font-family: monospace;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <img src="woodle-logo.jpeg" alt="Woodle Logo" data-test-woodle-logo>
            <h1>Event Summary</h1>
        </header>
        <main>
            <div class="event-details" data-test="event-details">
                <div class="detail-group">
                    <div class="detail-label">Organizer:</div>
                    <div class="detail-value">${pollData.get("name") != null ? pollData.get("name") : ""}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Email:</div>
                    <div class="detail-value">${pollData.get("email") != null ? pollData.get("email") : ""}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Activity Title:</div>
                    <div class="detail-value">${pollData.get("activityTitle") != null ? pollData.get("activityTitle") : ""}</div>
                </div>
                <div class="detail-group">
                    <div class="detail-label">Description:</div>
                    <div class="detail-value">${pollData.get("description") != null ? pollData.get("description") : ""}</div>
                </div>
                @if(pollData.get("eventDate") != null || pollData.get("timeSlot1") != null || pollData.get("timeSlot2") != null || pollData.get("timeSlot3") != null || pollData.get("timeSlot4") != null)
                <div class="detail-group" data-test="time-slots">
                    <div class="detail-label">Event Schedule:</div>
                    <div class="detail-value">
                        @if(pollData.get("eventDate") != null)Date: ${pollData.get("eventDate")}<br>@endif
                        @if(pollData.get("timeSlot1") != null)${pollData.get("timeSlot1")}<br>@endif
                        @if(pollData.get("timeSlot2") != null)${pollData.get("timeSlot2")}<br>@endif
                        @if(pollData.get("timeSlot3") != null)${pollData.get("timeSlot3")}<br>@endif
                        @if(pollData.get("timeSlot4") != null)${pollData.get("timeSlot4")}<br>@endif
                    </div>
                </div>
                @endif
                @if(pollData.get("expiryDate") != null)
                <div class="detail-group" data-test="expiry-info">
                    <div class="detail-label">Expiry Date:</div>
                    <div class="detail-value">${pollData.get("expiryDate")}</div>
                </div>
                @endif
            </div>
            
            <div class="shareable-url" data-test="shareable-url">
                <div class="detail-label">Shareable Event URL:</div>
                <div class="detail-value">http://localhost:8080/event/${uuid}</div>
                <input type="text" class="url-input" value="http://localhost:8080/event/${uuid}" readonly data-test="event-url">
            </div>
        </main>
    </div>
</body>
</html>