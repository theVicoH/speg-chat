package com.example.api.dtos;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserRoomDto {
    private String roomName;  // Nom de la salle
    private List<UserInfo> users;  // Liste des utilisateurs avec leur rôle

    @Data
    @Accessors(chain = true)
    public static class UserInfo {
        private String username;
        private String role;
    }
}
