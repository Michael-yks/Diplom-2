import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTest {
    private UserClient userClient;
    private User user;
    private Boolean isUserCreated;
    private Boolean isUserLogin;
    private int actualStatusCode;
    private String actualMessage;
    private String token;
    private String BearerToken;
    private final static String ERROR_MESSAGE_403 = "User already exists";
    private final static String ERROR_MESSAGE_REQUIRED_FIELDS_403 = "Email, password and name are required fields";
    private final static String ERROR_MESSAGE_401 = "email or password are incorrect";
    private final static String ERROR_MESSAGE_AUTH_401 = "You should be authorised";
    private final static String ERROR_MESSAGE_400_REQUIRED = "Недостаточно данных для входа";

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getDefault();
    }

    @After
    public void cleanUp() {
        // удаление пользователя
        userClient.delete(token);
    }

    @Test
    @DisplayName("Create user") // имя теста
    @Description("Basic test for post request to /api/auth/register") // описание теста
    public void userCreatedPositive() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        actualStatusCode = responseCreate.extract().statusCode();
        isUserCreated = responseCreate.extract().path("success");
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isUserCreated);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        Boolean isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);
    }

    @Test
    @DisplayName("Create user with the same credentials") // имя теста
    @Description("Basic test for post request to /api/auth/register") // описание теста
    public void sameUserCreatedNegative() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        isUserCreated = responseCreate.extract().path("success");
        assertTrue(isUserCreated);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        Boolean isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);

        // создание пользователя с теми же регистрационными данными
        responseCreate = userClient.create(user);
        actualStatusCode = responseCreate.extract().statusCode();
        actualMessage = responseCreate.extract().path("message");
        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertEquals(ERROR_MESSAGE_403, actualMessage);
    }

    @Test
    @DisplayName("Create user with missed required field - email") // имя теста
    @Description("Basic test for post request to /api/auth/register") // описание теста
    public void userCreatedWithoutEmailNegative() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        isUserCreated = responseCreate.extract().path("success");
        assertTrue(isUserCreated);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        Boolean isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);

        // создание пользователя без логина
        user.setEmail(null);
        responseCreate = userClient.create(user);
        actualStatusCode = responseCreate.extract().statusCode();
        actualMessage = responseCreate.extract().path("message");
        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertEquals(ERROR_MESSAGE_REQUIRED_FIELDS_403, actualMessage);
    }

    @Test
    @DisplayName("Create user with missed required field - password") // имя теста
    @Description("Basic test for post request to /api/auth/register") // описание теста
    public void userCreatedWithoutPasswordNegative() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        isUserCreated = responseCreate.extract().path("success");
        assertTrue(isUserCreated);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        Boolean isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);

        // создание пользователя без пароля
        user.setPassword(null) ;
        responseCreate = userClient.create(user);
        actualStatusCode = responseCreate.extract().statusCode();
        actualMessage = responseCreate.extract().path("message");
        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertEquals(ERROR_MESSAGE_REQUIRED_FIELDS_403, actualMessage);
    }

    @Test
    @DisplayName("Create user with missed required field - name") // имя теста
    @Description("Basic test for post request to /api/auth/register") // описание теста
    public void userCreatedWithoutNameNegative() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        isUserCreated = responseCreate.extract().path("success");
        assertTrue(isUserCreated);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        Boolean isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);

        // создание пользователя без имени
        user.setName(null);
        responseCreate = userClient.create(user);
        actualStatusCode = responseCreate.extract().statusCode();
        actualMessage = responseCreate.extract().path("message");
        assertEquals(SC_FORBIDDEN, actualStatusCode);
        assertEquals(ERROR_MESSAGE_403, actualMessage);
    }

    @Test
    @DisplayName("Login user") // имя теста
    @Description("Basic test for post request to api/auth/login") // описание теста
    public void userLoginPositive() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя
        ValidatableResponse responseLogin = userClient.login(Credential.from(user));
        actualStatusCode = responseLogin.extract().statusCode();
        isUserLogin = responseLogin.extract().path("success");
        assertTrue(isUserLogin);
        assertEquals(SC_OK, actualStatusCode);
    }

    @Test
    @DisplayName("Login user with wrong login") // имя теста
    @Description("Basic negative test for post request to api/auth/login") // описание теста
    public void userWrongEmail() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        // изменение e-mail
        user.setEmail("1");

        // логин пользователя с неправильным e-mail
        ValidatableResponse responseWrongLogin = userClient.login(Credential.from(user));
        actualStatusCode = responseWrongLogin.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        actualMessage = responseWrongLogin.extract().path("message");
        assertEquals(ERROR_MESSAGE_401, actualMessage);
    }

    @Test
    @DisplayName("Login user with wrong password") // имя теста
    @Description("Basic negative test for post request to api/auth/login") // описание теста
    public void userWrongPassword() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        // изменение пароля
        user.setPassword("1");

        //  логин пользователя с неправильным паролем
        ValidatableResponse responseWrongLogin = userClient.login(Credential.from(user));
        actualStatusCode = responseWrongLogin.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        actualMessage = responseWrongLogin.extract().path("message");
        assertEquals(ERROR_MESSAGE_401, actualMessage);
    }

    @Test
    @DisplayName("Edit user with no autorization") // имя теста
    @Description("Basic negative test for patch request to api/auth/user") // описание теста
    public void editEmailUserNoAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        String email = "sdf+1265@yandex.ru";
        //  редактирование почты пользователя
        user.setEmail(email);
        ValidatableResponse responseEdit = userClient.edit(user,"");
        actualStatusCode = responseEdit.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        actualMessage = responseEdit.extract().path("message");
        assertEquals(ERROR_MESSAGE_AUTH_401, actualMessage);
    }

    @Test
    @DisplayName("Edit user with no autorization") // имя теста
    @Description("Basic negative test for patch request to api/auth/user") // описание теста
    public void editNameUserNoAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        String name = "losos";
        //  редактирование имени пользователя
        user.setName(name);
        ValidatableResponse responseEdit = userClient.edit(user,"");
        actualStatusCode = responseEdit.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        actualMessage = responseEdit.extract().path("message");
        assertEquals(ERROR_MESSAGE_AUTH_401, actualMessage);
    }

    @Test
    @DisplayName("Edit user with autorization") // имя теста
    @Description("Basic positive test for patch request to api/auth/user") // описание теста
    public void editEmailUserWithAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        String name = "losos";
        //  редактирование имени пользователя
        user.setName(name);
        ValidatableResponse responseEdit = userClient.edit(user,token);

        actualStatusCode = responseEdit.extract().statusCode();
        assertEquals(SC_OK, actualStatusCode);
        Boolean isUserEdited = responseEdit.extract().path("success");
        assertTrue(isUserEdited);
        ResponseUser responseUser = responseEdit.extract().body().as(ResponseUser.class);
        assertEquals(user.getName(), responseUser.getUser().getName());
    }

    @Test
    @DisplayName("Edit user with no autorization") // имя теста
    @Description("Basic negative test for patch request to api/auth/user") // описание теста
    public void editPasswordUserNoAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        String password = "losos";
        //  редактирование пароля пользователя
        user.setPassword(password);
        ValidatableResponse responseEdit = userClient.edit(user,"");
        actualStatusCode = responseEdit.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        actualMessage = responseEdit.extract().path("message");
        assertEquals(ERROR_MESSAGE_AUTH_401, actualMessage);
    }

    @Test
    @DisplayName("Edit user with autorization") // имя теста
    @Description("Basic positive test for patch request to api/auth/user") // описание теста
    public void editPasswordUserWithAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // логин пользователя с корректными данными
        ValidatableResponse responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);

        String password = "losos";
        //  редактирование пароля пользователя
        user.setPassword(password);
        ValidatableResponse responseEdit = userClient.edit(user,token);

        actualStatusCode = responseEdit.extract().statusCode();
        assertEquals(SC_OK, actualStatusCode);
        Boolean isUserEdited = responseEdit.extract().path("success");
        assertTrue(isUserEdited);

        // логин пользователя с новым паролем
        responseCorrectLogin = userClient.login(Credential.from(user));
        isUserLogin = responseCorrectLogin.extract().path("success");
        assertTrue(isUserLogin);
    }
}
