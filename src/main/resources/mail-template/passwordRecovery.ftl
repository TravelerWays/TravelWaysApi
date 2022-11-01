<#-- @ftlvariable name="hash" type="java.lang.String" -->
<#-- @ftlvariable name="frontAppUrl" type="java.lang.String" -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <title>password recovery</title>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
        }

        .box {
            display: flex;
            align-content: center;
            justify-content: center;
            width: 100%;
        }

        .title {
            padding-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="box">
    <div class="content">
        <p class="title">Welcome, </p>
        <p>We received your request to reset a password. Please click <a
                    href="${frontAppUrl}/password-recovery/${hash}" target="_blank">here</a> or copy below link into your
            browser </p>

        <a href="${frontAppUrl}/password-recovery/${hash}" target="_blank">${frontAppUrl}/password-recovery/${hash}</a>

        <p>if it's not you start the process, please contact with us <a href="mailto:app-travelWays@outlook.com">app-travelWays@outlook.com</a>
        </p>
    </div>
</div>
</body>
</html>