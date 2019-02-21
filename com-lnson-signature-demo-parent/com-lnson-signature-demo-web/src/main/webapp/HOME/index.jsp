<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="func" %>

<c:set var="root" value="${pageContext.request.contextPath}"></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>HTTP接口签名 - 测试</title>
    <script type="text/javascript" src="${root}/SCRIPTS/json3.min.js"></script>
    <script type="text/javascript" src="${root}/SCRIPTS/jquery-1.10.2.js"></script>
    <script type="text/javascript" language="javascript">
        var ajaxPostJsonRequest = function (_httpUrl, _params, _callback) {
            $.ajax({
                cache: false,
                async: true,
                type: "POST",
                contentType: "application/json; charset=utf-8",
                url: _httpUrl,
                data: JSON.stringify(_params),
                dataType: "json",
                beforeSend: function (xhr) {
                    // 在发送请求之前，执行这里的代码
                },
                success: _callback, // function (result, status, xhr) { },
                complete: function (xhr, status) {
                    // 请求完成时运行的函数
                    // 在请求成功或失败之后均调用，即在 success 和 error 函数之后
                },
                error: function (xhr, status, error) {
                    // 如果请求失败要运行的函数。
                    if (typeof (xhr.responseText) != "undefined")
                        alert(xhr.status + ":" + xhr.statusText);
                    else
                        alert(error.message);
                }
            });
        };

        var ajaxPostKeyValueRequest = function (_httpUrl, _params, _callback) {
            $.ajax({
                cache: false,
                async: false,
                type: "POST",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                url: _httpUrl,
                data: _params,
                dataType: "json",
                beforeSend: function (xhr) {
                    // 在发送请求之前，执行这里的代码
                },
                success: _callback, // function (result, status, xhr) { },
                complete: function (xhr, status) {
                    // 请求完成时运行的函数
                    // 在请求成功或失败之后均调用，即在 success 和 error 函数之后
                },
                error: function (xhr, status, error) {
                    // 如果请求失败要运行的函数。
                    if (typeof (xhr.responseText) != "undefined")
                        alert(xhr.status + ":" + xhr.statusText);
                    else
                        alert(error.message);
                }
            });
        };
    </script>
</head>
<body>
<div>
    <h2>测试HTTP接口签名</h2>
</div>
</body>
</html>