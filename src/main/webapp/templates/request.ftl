<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">

<html>
<head>
    <meta charset="UTF-8">
    <title>Overclockers fetcher</title>
    <link rel="stylesheet" type="text/css" href="/resources/css/main.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>

<body>
<div>
    <h1>Форма поиска</h1>

    <form class="requestForm" action="/request" method="post">
        <label>Что ищем:</label>
        <input type="text" name="searchRequest" placeholder="запрос для поиска"/>
        <input class="button" type="submit" value="Создать"/>
    </form>

    <div class="message">
        <#if errorMessage??>
            <h3>${errorMessage}</h3>
        </#if>
        <#if successMessage??>
            <h4>${successMessage}</h4>
        </#if>
    </div>
</div>
    <h2>Созданные запросы</h2>
    <div>
        <table class="table">
            <#list requestsList as request>
                <tr>
                    <td>${request.request}</td>
                    <td>${request.createdDateTime}</td>
                    <td>
                        <button name="index" onclick="window.location.href='/request/remove/${request.requestId}'">
                            Удалить
                        </button>
                    </td>
                </tr>
            </#list>
        </table>
    </div>
</div>


</body>

</html>