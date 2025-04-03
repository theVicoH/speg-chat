package com.example.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class RoomUpdateDto {
    @JsonProperty(access = READ_ONLY)
    private Integer id;

    @NotBlank(message = "Room name is required")
    private String name;
    
    // We can keep userIds to allow adding users during update
    private List<Integer> userIds;

    @JsonProperty(access = READ_ONLY)
    private Integer creatorId;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime updatedAt;
}