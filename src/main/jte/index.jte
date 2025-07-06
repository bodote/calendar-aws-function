<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Woodle</title>
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
        .button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 16px;
        }
        .button:hover {
            background-color: #0056b3;
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
    </style>
</head>
<body>
    <div class="container">
        <header>
            <img src="/woodle-logo.jpeg" alt="Woodle Logo" data-test-woodle-logo>
            <h1>Woodle</h1>
        </header>
        <main>
            <a href="/schedule-event" class="button" data-test-schedule-event-button>Schedule Event</a>
        </main>
    </div>
</body>
</html> 