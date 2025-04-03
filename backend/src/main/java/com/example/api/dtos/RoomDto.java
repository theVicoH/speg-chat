package com.example.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RoomDto {
    @JsonProperty(access = READ_ONLY)
    private Integer id;

    @NotBlank(message = "Room name is required")
    private String name;

    @NotNull(message = "Room type is required")
    private Integer typeId;

    @JsonProperty(access = READ_ONLY)
    private Integer creatorId;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime updatedAt;
}
