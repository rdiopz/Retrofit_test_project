package tests;

import base.BaseApiTest;
import io.qameta.allure.*;
import models.get.SingleUserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import retrofit2.Response;

import java.io.IOException;

@Epic("Управление пользователями")
@Feature("Пользователь")
@Story("Получение пользователя")
@Owner("AlexeyRDIO")
@Severity(SeverityLevel.NORMAL)
public class UserByIdTest extends BaseApiTest {
    @ParameterizedTest(name = "Получение пользователя по идентификатору: {0}")
    @CsvSource({"1", "2"}) // Данные для тестирования
    @Description("Проверка, что данные пользователя получены или нет.")
    public void testGetUserById(@Param("Идентификатор") int id) throws IOException{
        // Выполнение запроса
        Response<SingleUserResponse> response = executeGetUser(id);

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверки по полученному телу в запросе
        SingleUserResponse root = validateResponse(response);
        assertUserDetails(id, root.getData().getId());
    }

    @Step("Выполнение запроса на получения пользователя по идентификатору")
    private Response<SingleUserResponse> executeGetUser(int id) throws IOException {
        return userService.getUserById(id).execute();
    }

    @Step("Проверка ответа сервера")
    private SingleUserResponse validateResponse(Response<SingleUserResponse> response) {
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при обновлении пользователя");

        SingleUserResponse root = response.body();
        Assertions.assertNotNull(root, "Ответ не содержит тела");
        return root;
    }

    @Step("Проверка полученных данных: совпадение идентификатора или нет.")
    private void assertUserDetails(int id, Integer expectedId) {
        Assertions.assertEquals(id, expectedId, "Идентификатор пользователя " +
                "не соответствует ожидаемому");
    }
}
