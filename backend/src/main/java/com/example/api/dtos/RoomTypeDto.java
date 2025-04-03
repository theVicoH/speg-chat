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
public class RoomTypeDto {
    
    @JsonProperty(access = READ_ONLY)
    private Integer id;

    @NotBlank(message = "Room type is required")
    private String type;

}
