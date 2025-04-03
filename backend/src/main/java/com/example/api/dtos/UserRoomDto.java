package com.example.api.dtos;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserRoomDto {
    private String roomName;
    private List<UserInfo> users;
    @Data
    @Accessors(chain = true)
    public static class UserInfo {
        private Integer id;
        private String username;
        private String role;
        private boolean blocked;

    }
}
