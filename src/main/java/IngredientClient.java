import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientClient extends Client {

    private static final String PATH = "api/ingredients";

    @Step("Получение ингредиентов")
    public ValidatableResponse get() {
        return given()
                .spec(getSpec())
                .when()
                .get(PATH)
                .then();
    }
}