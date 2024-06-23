package models.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @AllArgsConstructor @ToString
public class UserUpdateResponse {
    private String name;
    private String job;
    private String updatedAt;
}
