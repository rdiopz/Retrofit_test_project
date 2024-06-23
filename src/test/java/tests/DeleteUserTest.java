package tests;

import base.BaseApiTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import retrofit2.Response;
import java.io.IOException;

@Epic("Управление пользователями")
@Feature("Пользователь")
@Story("Удаления пользователя")
@Owner("AlexeyRDIO")
public class DeleteUserTest extends BaseApiTest {
    @ParameterizedTest(name = "Удаление пользователя с идентификатором {0}")
    @CsvSource({"2", "3"})  // Данные для тестирования
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка, что данные пользователя удалены.")
    public void testDeleteUser(@Param("Идентификатор") int id) throws IOException {
        // Выполняем запрос на удаление
        Response<?> response = executeUserDelete(id);

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверка запроса
        validateResponse(response);
    }
    @Step("Выполнение запроса на удаление пользователя")
    private Response<?> executeUserDelete(int id) throws IOException {
        return userService.deleteUserById(id).execute();
    }

    @Step("Проверка на успешность запроса")
    private void validateResponse(Response<?> response) {
        // Проверяем, что запрос выполнен успешно и код возврата равен 204
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при удалении пользователя");
        Assertions.assertEquals(204, response.code(), "Код возврата не 204");
    }

}
