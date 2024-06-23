package tests;

import base.BaseApiTest;
import io.qameta.allure.*;
import models.additional.UserRequest;
import models.create.CreateUserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

@Epic("Управление пользователями")
@Feature("Пользователь")
@Story("Создание пользователя")
@Owner("AlexeyRDIO")
public class CreateUserTest extends BaseApiTest {
    // Погрешность в 1 минуту
    private final int deviation = 1;
    // Паттерн времени - регулярное выражение
    private final String patternTime = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";

    // Данные для тестирования
    private static Stream<UserRequest> createUserData() {
        return Stream.of(new UserRequest("Алексей", "Тестировщик"));
    }

    @ParameterizedTest(name = "Создание пользователя с данными: {0} ")
    @MethodSource("createUserData")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка, что пользователь создан.")
    public void testCreateUser(@Param("Пользовательские данные") UserRequest userRequest) throws IOException {
        // Время до запроса
        OffsetDateTime beforeRequest = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(deviation);

        // Выполнение запроса
        Response<CreateUserResponse> response = executeUserCreation(userRequest);

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверки по полученному телу в запросе
        CreateUserResponse root = validateResponse(response);
        checkResponseTime(root.getCreatedAt(), beforeRequest);
        assertUserDetails(userRequest, root);
    }
    
    @Step("Выполнение запроса на создание пользователя")
    private Response<CreateUserResponse> executeUserCreation(UserRequest userRequest) throws IOException {
        return userService.createUser(userRequest).execute();
    }

    @Step("Проверка ответа сервера")
    private CreateUserResponse validateResponse(Response<CreateUserResponse> response) {
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при добавлении пользователя");

        CreateUserResponse root = response.body();
        Assertions.assertNotNull(root, "Ответ не содержит тела");
        return root;
    }

    @Step("Проверка соответствия времени создания пользователя ожидаемому")
    private void checkResponseTime(String responseTime, OffsetDateTime beforeRequest) {
        Assertions.assertTrue(responseTime.matches(patternTime),
                "Не совпадает формат времени");

        OffsetDateTime createdAt = OffsetDateTime.parse(responseTime);
        // Время после запроса
        OffsetDateTime afterRequest = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(deviation);

        Assertions.assertTrue(!createdAt.isBefore(beforeRequest) && !createdAt.isAfter(afterRequest),
                "Время создания пользователя не соответствует ожидаемому временному окну запроса");
    }

    @Step("Проверка, что данные пользователя соответствуют ожидаемым")
    private void assertUserDetails(UserRequest expected, CreateUserResponse actual) {
        Assertions.assertEquals(expected.getName(), actual.getName(),
                "Имя пользователя в ответе не соответствует отправленному");
        Assertions.assertEquals(expected.getJob(), actual.getJob(),
                "Работа пользователя в ответе не соответствует отправленному");
    }
}