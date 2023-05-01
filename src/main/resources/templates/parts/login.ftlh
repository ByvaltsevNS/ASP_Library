<#macro login path isRegisterForm>
    <div class="container-fluid text-center mt-5">
        <form action="${path}" method="post" style="max-width: 350px; margin: 0 auto;">
            <div class="border border-secondary p-3 rounded">
                <p style="color: #6c757d"><#if isRegisterForm>Создание пользователя<#else>Авторизация</#if></p>
                <p><input type="text" name="username" placeholder="Логин" class="form-control" required autofocus/></p>
                <p><input type="password" name="password" placeholder="Пароль" class="form-control" required/></p>
                <p><input type="hidden" name="_csrf" value="${_csrf.token}" /></p>
                <#if isRegisterForm>
                    <p><input name="email" placeholder="Email" class="form-control" required/></p>
                    <p><input name="firstName" placeholder="Имя" class="form-control" required/></p>
                    <p><input name="lastName" placeholder="Фамилия" class="form-control" required/></p>
                    <p><input name="groupId" placeholder="Группа" class="form-control" required/></p>
                    <p><input name="studentId" placeholder="Номер студенческого" class="form-control" required/></p>

                    <p><input class="btn btn-primary" type="submit" value="Зарегистрировать" /></p>
                <#else>
                    <p><input class="btn btn-primary" type="submit" value="Войти" /></p>
                    <p><a href="/registration">Зарегистрироваться</a></p>
                </#if>
            </div>
        </form>
    </div>
</#macro>

<#macro logout>
    <form action="/logout" method="post">
        <input type="submit" value="Sign Out"/>
    </form>
</#macro>