import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OrderTest {
    private IngredientClient ingredientClient;
    private OrderClient orderClient;
    private int actualStatusCode;
    private Boolean isOrderCreated;
    private UserClient userClient;
    private User user;
    private String token;
    private String BearerToken;
    private final static String ERROR_MESSAGE_AUTH_401 = "You should be authorised";

    @Before
    public void setUp() {
        ingredientClient = new IngredientClient();
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = UserGenerator.getDefault();
    }

    @After
    public void cleanUp() {
        // удаление пользователя
        if (token != null) {userClient.delete(token);}
    }

    @Test
    @DisplayName("Create order with no auth") // имя теста
    @Description("Basic test for post request to api/orders") // описание теста
    public void orderCreateNoAuth() {
        // получение списка ингредиентов
        ValidatableResponse responseGetIngredients = ingredientClient.get();
        ResponseIngredients responseIngredients = responseGetIngredients.extract().body().as(ResponseIngredients.class);
        ArrayList<Ingredient> list = responseIngredients.getData();
        ArrayList<String> listString = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listString.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listString);


        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,"");
        actualStatusCode = responseCreateOrder.extract().statusCode();
        isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isOrderCreated);
    }

    @Test
    @DisplayName("Create order with auth") // имя теста
    @Description("Basic test for post request to api/orders") // описание теста
    public void orderCreateWithAuth() {
        // получение списка ингредиентов
        ValidatableResponse responseGetIngredients = ingredientClient.get();
        ResponseIngredients responseIngredients = responseGetIngredients.extract().body().as(ResponseIngredients.class);
        ArrayList<Ingredient> list = responseIngredients.getData();
        ArrayList<String> listString = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listString.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listString);

        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,token);
        actualStatusCode = responseCreateOrder.extract().statusCode();
        isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isOrderCreated);
    }

    @Test
    @DisplayName("Create order with no auth and no ingredients") // имя теста
    @Description("Basic test for post request to api/orders") // описание теста
    public void orderCreateNoAuthNoIngredients() {
        ArrayList<String> listString = new ArrayList<>();
        ListIngredient listIngredient = new ListIngredient(listString);

        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,"");
        actualStatusCode = responseCreateOrder.extract().statusCode();
        isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals(SC_BAD_REQUEST, actualStatusCode);
        assertFalse(isOrderCreated);
    }

    @Test
    @DisplayName("Create order with auth") // имя теста
    @Description("Basic test for post request to api/orders") // описание теста
    public void orderCreateWithAuthWrongIngredients() {
        // получение списка ингредиентов
        ValidatableResponse responseGetIngredients = ingredientClient.get();
        ResponseIngredients responseIngredients = responseGetIngredients.extract().body().as(ResponseIngredients.class);
        ArrayList<Ingredient> list = responseIngredients.getData();
        ArrayList<String> listString = new ArrayList<>();
        int max = list.size();
        Random random = new Random();
        for (int i = 0; i < max; i++)
        {
            listString.add(list.get(i).get_id()+random.nextInt(1000));
        }
        ListIngredient listIngredient = new ListIngredient(listString);

        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,token);
        actualStatusCode = responseCreateOrder.extract().statusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }

    @Test
    @DisplayName("Get order with auth") // имя теста
    @Description("Basic test for get request to api/orders") // описание теста
    public void ordersGetWithAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // получение списка ингредиентов
        ValidatableResponse responseGetIngredients = ingredientClient.get();
        ResponseIngredients responseIngredients = responseGetIngredients.extract().body().as(ResponseIngredients.class);
        ArrayList<Ingredient> list = responseIngredients.getData();
        ArrayList<String> listString = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listString.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listString);

        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,token);
        actualStatusCode = responseCreateOrder.extract().statusCode();
        isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isOrderCreated);

        // получение заказов пользователя
        ValidatableResponse responseGetOrder = orderClient.get(token);
        actualStatusCode = responseGetOrder.extract().statusCode();
        Boolean isOrderGet = responseGetOrder.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isOrderGet);
    }

    @Test
    @DisplayName("Get order with no auth") // имя теста
    @Description("Basic test for get request to api/orders") // описание теста
    public void ordersGetWithNoAuth() {
        // создание пользователя
        ValidatableResponse responseCreate = userClient.create(user);
        BearerToken = responseCreate.extract().path("accessToken");
        token = BearerToken.substring(7);

        // получение списка ингредиентов
        ValidatableResponse responseGetIngredients = ingredientClient.get();
        ResponseIngredients responseIngredients = responseGetIngredients.extract().body().as(ResponseIngredients.class);
        ArrayList<Ingredient> list = responseIngredients.getData();
        ArrayList<String> listString = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listString.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listString);

        // создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(listIngredient,token);
        actualStatusCode = responseCreateOrder.extract().statusCode();
        isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isOrderCreated);

        // получение заказов пользователя
        ValidatableResponse responseGetOrder = orderClient.get("");
        actualStatusCode = responseGetOrder.extract().statusCode();
        Boolean isOrderGet = responseGetOrder.extract().path("success");
        String message = responseGetOrder.extract().path("message");
        assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        assertFalse(isOrderGet);
        assertEquals(ERROR_MESSAGE_AUTH_401,message);
    }
}
