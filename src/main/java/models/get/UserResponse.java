package models.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @AllArgsConstructor @ToString
public class UserResponse {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
}
