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
        input[type="text"], input[type="email"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
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
            <form data-test-schedule-event-form>
                <div>
                    <label for="yourName">Your name:</label>
                    <input type="text" id="yourName" name="yourName" data-test-your-name-field required>
                </div>
                <div>
                    <label for="emailAddress">Email address:</label>
                    <input type="email" id="emailAddress" name="emailAddress" data-test-email-field required>
                </div>
            </form>
        </main>
    </div>
</body>
</html> 