import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Client {
    private static final String BASE_URL= "https://stellarburgers.nomoreparties.site/";
    protected static final List<Filter> list = new ArrayList<>(
            Arrays.asList(
                    new RequestLoggingFilter(),
                    new ResponseLoggingFilter()
            )
    );

    protected static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .addFilters(list)
                .build();
    }
}
