@param java.util.Map<String, String> pollData = null
@param String uuid

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Event - Step 3 - Woodle</title>
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
        input[type="date"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }
        .navigation {
            margin-top: 20px;
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
        .create-button {
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .create-button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <img src="/woodle-logo.jpeg" alt="Woodle Logo" data-test-woodle-logo>
            <h1>Schedule Event - Step 3</h1>
        </header>
        <main>
            <form data-test-schedule-event-step3-form method="post" action="/schedule-event-step3/${uuid}">
                <div class="form-group">
                    <label for="expiryDate">Expiry Date:</label>
                    <input type="date" id="expiryDate" name="expiryDate" 
                           value="${pollData != null && pollData.get("expiryDate") != null ? pollData.get("expiryDate") : ""}"
                           required>
                </div>
                <div class="navigation">
                    <button type="submit" name="action" value="back" class="back-button" data-test-back-button>Back</button>
                    <button type="submit" name="action" value="create-poll" class="create-button" data-test-create-poll-button>Create the poll</button>
                </div>
            </form>
        </main>
    </div>
</body>
</html> 