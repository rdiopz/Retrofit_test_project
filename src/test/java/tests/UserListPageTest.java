package tests;

import base.BaseApiTest;
import io.qameta.allure.*;
import models.get.UserListResponse;
import models.get.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

@Epic("Управление пользователями")
@Feature("Страницы пользователей")
@Story("Проверка списка пользователей")
@Owner("AlexeyRDIO")
public class UserListPageTest extends BaseApiTest {
    @ParameterizedTest(name = "Тест страницы {0} ожидается пустой список: {1}")
    @CsvSource({
            "2, false",
            "99999999, true"
    })  // Данные для тестирования
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка, что список пользователей на указанной странице соответствует ожиданию: пустой или нет.")
    public void testPageWithUsers(@Param("Страница") int page, @Param("Ожидание-пустота") boolean expectEmpty) throws IOException {
        // Выполнение запроса на получение списка пользователей
        Response<UserListResponse> response = executeGetUserList(page);

        // Прикрепление ответа API к отчёту
        logResponse(response.body());

        // Проверки по полученному телу в запросе
        UserListResponse root = validateResponse(response);
        checkPageResponse(root, page);
        checkUsersData(root.getData(), expectEmpty, page);

    }

    @Step("Выполнение запроса на получение списка пользователей")
    private Response<UserListResponse> executeGetUserList(int page) throws IOException {
        return userService.getUserList(page).execute();
    }

    @Step("Проверка ответа сервера")
    private UserListResponse validateResponse(Response<UserListResponse> response) {
        Assertions.assertTrue(response.isSuccessful(), "Ошибка запроса при обновлении пользователя");

        UserListResponse root = response.body();
        Assertions.assertNotNull(root, "Ответ не содержит тела");
        return root;
    }

    @Step("Проверка схожести запрошенной страницы и полученной в ответе")
    private void checkPageResponse(UserListResponse root, int page) {
        Assertions.assertEquals(page, root.getPage(), "Запрошенная страница не соответствует полученной");
    }

    @Step("Проверка списка пользователей")
    private void checkUsersData(List<UserResponse> userData, boolean expectEmpty, int page) {
        Assertions.assertEquals(expectEmpty, userData.isEmpty(),
                        expectEmpty ? "Список пользователей не пуст на странице " + page
                        : "Список пользователей пуст на странице " + page);
    }
}