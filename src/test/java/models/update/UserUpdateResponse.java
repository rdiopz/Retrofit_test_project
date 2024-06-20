package models.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateResponse {
    private String name;
    private String job;
    private String updatedAt;
}
