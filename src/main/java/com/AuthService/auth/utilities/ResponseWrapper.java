package com.AuthService.auth.utilities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm dd-MM-yy")
    private LocalDateTime time;
    private int status;
    private String message;
    private T data;
    private Boolean isError;
}
