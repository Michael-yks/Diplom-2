import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client{

    private static final String PATH = "api/orders";

    @Step("Создание заказа")
    public ValidatableResponse create(ListIngredient listIngredient, String token) {
        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .body(listIngredient)
                .when()
                .post(PATH)
                .then();
    }

    @Step("Получение заказов")
    public ValidatableResponse get(String token) {
        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .when()
                .get(PATH)
                .then();
    }

}
