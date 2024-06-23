package models.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @AllArgsConstructor @ToString
public class UserListResponse {
    private Integer page;
    private Integer per_page;
    private Integer total;
    private Integer total_pages;
    private List<UserResponse> data;
    private Support support;
}
