package services;

import models.update.UserUpdateResponse;
import models.create.CreateUserResponse;
import models.get.SingleUserResponse;
import models.get.UserListResponse;
import models.additional.UserRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    // Метод получения списка пользователей
    @GET("api/users")
    Call<UserListResponse> getUserList (@Query("page") int pageParameter);

    // Метод получения пользователя по идентификатору
    @GET("api/users/{id}")
    Call<SingleUserResponse> getUserById(@Path("id") int idParameter);

    // Метод создания пользователя по имени и работе
    @POST("api/users")
    Call<CreateUserResponse> createUser(@Body UserRequest userRequest);

    // Метод обновления пользователя по имени и работе
    @PUT("api/users/{id}")
    Call<UserUpdateResponse> updateUserById(@Path("id") int idParameter, @Body UserRequest userRequest);

    // Метод удаления пользователя по идентификатору
    @DELETE("api/users/{id}")
    Call<Void> deleteUserById(@Path("id") int idParameter);

}
