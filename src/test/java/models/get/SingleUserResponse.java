package models.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SingleUserResponse {
    private UserResponse data;
    private Support support;
}
