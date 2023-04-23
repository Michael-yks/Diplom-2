import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    private static final String PATH_CREATE = "api/auth/register";
    private static final String PATH = "api/auth/user";
    private static final String PATH_LOGIN = "api/auth/login";


    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step ("Логин пользователя")
    public ValidatableResponse login(Credential credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(PATH_LOGIN)
                .then();
    }

    @Step ("Обновление информации о пользователе с авторизацией")
    public ValidatableResponse edit(User user, String token) {
        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .body(user)
                .when()
                .patch(PATH)
                .then();
    }

    @Step ("Удаление пользователя")
    public ValidatableResponse delete(String token) {
        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .when()
                .delete(PATH)
                .then();
    }
}
