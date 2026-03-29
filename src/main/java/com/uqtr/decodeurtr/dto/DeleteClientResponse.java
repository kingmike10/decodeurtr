package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteClientResponse {
    private Long id;
    private boolean success;
    private String message;

    public DeleteClientResponse() {
    }

    public DeleteClientResponse(Long id,boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;
    }

}
