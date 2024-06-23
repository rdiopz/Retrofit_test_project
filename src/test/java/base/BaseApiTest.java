package base;

import com.google.gson.Gson;
import io.qameta.allure.Attachment;
import rest.ApiManager;
import services.UserService;

public class BaseApiTest {
    protected final ApiManager<UserService> apiManager;
    protected final UserService userService;

    public BaseApiTest() {
        apiManager = new ApiManager<>("https://reqres.in/", UserService.class);
        userService = apiManager.getService();
    }

    @Attachment(value = "Ответ API", type = "application/json")
    protected String logResponse(Object responseBody) {
        if (responseBody != null) {
            return new Gson().toJson(responseBody);
        }
        return "Нет данных";
    }
}
