package com.pro0inter.HeyDocServer.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data

public class RestErrorResponse {
    private int status;
    private String msg;

    public RestErrorResponse() {
    }

    public RestErrorResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public RestErrorResponse(HttpStatus status, String msg) {
        this.status = status.value();
        this.msg = msg;
    }

}
