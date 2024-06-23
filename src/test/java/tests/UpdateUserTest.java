package tests;

import base.BaseApiTest;
import io.qameta.allure.*;
import models.additional.UserRequest;
import models.update.UserUpdateResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import retrofit2.Response;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Epic("Управление пользователями")
@Feature("Пользователь")
@Story("Изменения пользователя")
@Owner("AlexeyRDIO")
public class UpdateUserTest extends BaseApiTest {
    // Погрешность в 1 минуту
    private final int deviation = 1;
    // Паттерн времени - регулярное выражение
    private final String patternTime = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";

    @ParameterizedTest(name = "Изменение пользователя {2} на новое имя: {0}, работу: {1} ")
    @CsvSource({
            "Алексей, Тестировщик, 2"
    })  // Данные для тестирования
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка, что данные пользователя обновлены.")
    public void testUpdateUser(@Param("Имя") String name,
                               @Param("Работа") String job,
                               @Param("Идентификатор") int id) throws IOException {
        // Создание тела для запроса
        UserRequest userRequest = new UserRequest(name, job);

        // Время до запроса
        OffsetDateTime beforeRequest = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(deviation);

        // Выполнение запроса на обновление пользователя
        Response<UserUpdateResponse> response = executeUserUpdate(id, userRequest);

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверки по полученному телу в запросе
        UserUpdateResponse root = validateResponse(response);
        assertUserDetails(name, job, root);
        checkResponseTime(root.getUpdatedAt(), beforeRequest);
    }

    @Step("Выполнение запроса на обновление пользователя с ID: {id}")
    private Response<UserUpdateResponse> executeUserUpdate(int id, UserRequest userRequest) throws IOException {
        return userService.updateUserById(id, userRequest).execute();
    }

    @Step("Проверка ответа сервера")
    private UserUpdateResponse validateResponse(Response<UserUpdateResponse> response) {
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при обновлении пользователя");

        UserUpdateResponse root = response.body();
        Assertions.assertNotNull(root, "Ответ не содержит тела");
        return root;
    }

    @Step("Проверка данных пользователя после обновления")
    private void assertUserDetails(String name, String job, UserUpdateResponse root) {
        Assertions.assertEquals(name, root.getName(),
                "Имя пользователя после обновления не соответствует ожидаемому");
        Assertions.assertEquals(job, root.getJob(),
                "Работа пользователя после обновления не соответствует ожидаемому");
    }

    @Step("Проверка, что время обновления соответствует ожидаемому временному окну")
    private void checkResponseTime(String responseTime, OffsetDateTime beforeRequest) {
        Assertions.assertTrue(responseTime.matches(patternTime),
                "Не совпадает формат времени");

        OffsetDateTime createdAt = OffsetDateTime.parse(responseTime);
        // Время после запроса
        OffsetDateTime afterRequest = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(deviation);

        Assertions.assertTrue(!createdAt.isBefore(beforeRequest) && !createdAt.isAfter(afterRequest),
                "Время создания пользователя не соответствует ожидаемому временному окну запроса");
    }
}