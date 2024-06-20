package tests;
import models.additional.UserRequest;
import models.create.CreateUserResponse;
import models.get.UserListResponse;
import models.get.UserResponse;
import models.update.UserUpdateResponse;
import services.UserService;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import io.qameta.allure.*;

public class UserTests {

    // Создание экземпляра Retrofit для отправки HTTP-запросов и чтения ответов.
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://reqres.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Создание объекта UserService с использованием экземпляра Retrofit для отправки запросов к API,
    // определенным в интерфейсе UserService. Это позволяет делать HTTP-запросы к API, описанные в интерфейсе, и получать ответы.
    private final UserService userService = retrofit.create(UserService.class);

    @Attachment(value = "Ответ API", type = "application/json")
    private String logResponse(Object responseBody) {
        if (responseBody != null) {
            return new Gson().toJson(responseBody);
        }
        return "Нет данных";
    }

    @ParameterizedTest(name = "Тест страницы {0} ожидается пустой список: {1}")
    @CsvSource({
            "2, false",
            "99999999, true"
    })
    @Epic("Управление пользователями")
    @Feature("Страницы пользователей")
    @Story("Проверка списка пользователей")
    @Owner("AlexeyRDIO")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка, что список пользователей на указанной странице соответствует ожиданию: пустой или нет.")
    public void testPageWithUsers(@Param("Страница") int page,
                                  @Param("Ожидание-пустота") boolean expectEmpty)
                                    throws IOException {
        // Выполнение запроса на получение списка пользователей
        Response<UserListResponse> response = userService.getUserList(page).execute();

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        //  Проверяем, что запрос выполнен успешно
        Assertions.assertTrue(response.isSuccessful(), "Ответ сервера не успешен на странице " + page);

        // Получение тело запросаx
        UserListResponse root = response.body();

        // Убедимся, что тело ответа не пустое
        Assertions.assertNotNull(root, "Ответ не содержит тела");

        // Проверяем схожесть страниц
        Assertions.assertEquals(page, root.getPage(), "Запрошенная страница не соответствует полученной");

        // Получем данные списка пользователей
        List<UserResponse> userData = root.getData();

        // Проверяем наличия списка относительно указанного ожидания
        if (expectEmpty) {
            Assertions.assertTrue(userData.isEmpty(), "Список пользователей не пуст на странице " + page);
        } else {
            Assertions.assertFalse(userData.isEmpty(), "Список пользователей пуст на странице " + page);
        }
    }

    private static Stream<UserRequest> createUserData() {
        return Stream.of(new UserRequest("Алексей", "Тестировщик"));
    }

    @ParameterizedTest(name = "Создание пользователя с данными: {0} ")
    @MethodSource("createUserData")
    @Epic("Управление пользователями")
    @Feature("Пользователь")
    @Story("Создание пользователя")
    @Owner("AlexeyRDIO")
    @Description("Проверка, что пользователь создан.")
    public void testCreateUser(@Param("Пользовательские данные") UserRequest userRequest) throws IOException {
        // Инициализация тестовых данных для проверки
        String patternTime =  "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";
        int deviation = 1;

        // Запоминаем время перед запросом и выполняем запрос на создание
        OffsetDateTime beforeRequest = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(deviation);
        Response<CreateUserResponse> response = userService.createUser(userRequest).execute();

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверяем, что запрос выполнен успешно и получаем тело ответа на запрос
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при добавлении пользователя");

        // Создаем тело
        CreateUserResponse root = response.body();

        // Убедимся, что тело ответа не пустое
        Assertions.assertNotNull(root, "Ответ не содержит тела");

        // Проверка на соответствие формата
        Assertions.assertTrue(root.getCreatedAt().matches(patternTime), "Не совпадает формат времени");

        // Парсим строку даты и запоминаем время после запроса
        OffsetDateTime createdAt = OffsetDateTime.parse(root.getCreatedAt());
        OffsetDateTime afterRequest = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(deviation);

        // Проверяем, что пользователь создался с таким же именем и работой
        Assertions.assertEquals(userRequest.getName(), root.getName(), "Имя пользователя в ответе не соответствует отправленному");
        Assertions.assertEquals(userRequest.getJob(), root.getJob(), "Работа пользователя в ответе не соответствует отправленному");

        // Проверяем, что createdAt лежит между временем до и после запроса
        Assertions.assertTrue(!createdAt.isBefore(beforeRequest) && !createdAt.isAfter(afterRequest),
                "Время обновления пользователя не соответствует ожидаемому временному окну запроса");

    }

    @ParameterizedTest(name = "Изменения пользователя {2} на новое имя: {0}, работу: {1} ")
    @CsvSource({
            "Алексей, Тестировщик, 2"
    })
    @Epic("Управление пользователями")
    @Feature("Пользователь")
    @Story("Изменения пользователя")
    @Owner("AlexeyRDIO")
    @Description("Проверка, что данные пользователя обновлены.")
    public void testUpdateUser(@Param("Имя") String name,
                               @Param("Работа") String job,
                               @Param("Идентификатор") int id) throws IOException {
        // Инициализация тестовых данных для проверки
        String patternTime =  "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";
        int deviation = 1;
        UserRequest userRequest = new UserRequest(name, job);

        // Запоминаем время перед запросом и выполняем запрос на обновление
        OffsetDateTime beforeRequest = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(deviation);
        Response<UserUpdateResponse> response = userService.updateUserById(id, userRequest).execute();

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверяем, что запрос выполнен успешно
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при обновлении пользователя");

        // Создаем тело
        UserUpdateResponse root = response.body();

        // Убедимся, что тело ответа не пустое
        Assertions.assertNotNull(root, "Ответ не содержит тела");

        // Проверка на соответствие формата
        Assertions.assertTrue(root.getUpdatedAt().matches(patternTime), "Не совпадает формат времени");

        // Парсим строку даты и запоминаем время после запроса
        OffsetDateTime updatedAt = OffsetDateTime.parse(root.getUpdatedAt());
        OffsetDateTime afterRequest = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(deviation);

        // Проверяем, что пользователь создался с таким же именем и работой
        Assertions.assertEquals(name, root.getName(), "Имя пользователя после обновления не соответствует ожидаемому");
        Assertions.assertEquals(job, root.getJob(), "Работа пользователя после обновления не соответствует ожидаемому");

        // Проверяем, что updatedAt лежит между временем до и после запроса
        Assertions.assertTrue(!updatedAt.isBefore(beforeRequest) && !updatedAt.isAfter(afterRequest),
                "Время обновления пользователя не соответствует ожидаемому временному окну запроса");

    }

    @ParameterizedTest(name = "Удаление пользователя с идентификатором {0}")
    @CsvSource({"2", "3"})
    @Epic("Управление пользователями")
    @Feature("Пользователь")
    @Story("Удаления пользователя")
    @Owner("AlexeyRDIO")
    @Description("Проверка, что данные пользователя удалены.")
    public void testDeleteUser(@Param("Идентификатор") int id) throws IOException {
        // Инициализация тестовых данных для проверки

        // Выполняем запрос на удаление
        Response<?> response = userService.deleteUserById(id).execute();

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверяем, что запрос выполнен успешно и код возврата равен 204
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при удалении пользователя");
        Assertions.assertEquals(204, response.code(), "Код возврата не 204");

    }
}