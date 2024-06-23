package models.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @AllArgsConstructor @ToString
public class SingleUserResponse {
    private UserResponse data;
    private Support support;
}
