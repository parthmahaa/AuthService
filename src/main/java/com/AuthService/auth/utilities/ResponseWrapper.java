package com.AuthService.auth.utilities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm dd-MM-yy")
    private LocalDate time;
    private int status;
    private String message;
    private T data;
    private Boolean isError;

    public ResponseWrapper(LocalDateTime now, int value, String errorMessage, Object data, boolean isError) {
    }
}
