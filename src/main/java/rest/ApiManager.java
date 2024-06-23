package rest;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Getter
public class ApiManager<Service> {
    private final Retrofit retrofit;
    private final Service service;

    // Публичный конструктор, который принимает URL и объект класса сервиса
    public ApiManager(String baseUrl, Class<Service> serviceClass) {
        // Создание Retrofit для отправки HTTP-запросов и чтения ответов.
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Создание сервиса с использованием Retrofit для отправки запросов к API.
        this.service = retrofit.create(serviceClass);
    }
}