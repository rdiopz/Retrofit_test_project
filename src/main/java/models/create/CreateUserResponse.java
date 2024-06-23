package models.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @AllArgsConstructor @ToString
public class CreateUserResponse {
    private String name;
    private String job;
    private Integer id;
    private String createdAt;
}
