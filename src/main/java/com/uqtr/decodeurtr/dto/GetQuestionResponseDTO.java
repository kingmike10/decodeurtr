package com.uqtr.decodeurtr.dto;
import lombok.Getter;
@Getter
public class GetQuestionResponseDTO {
    private final boolean success;
    private final String question;
    private final String message;
    public GetQuestionResponseDTO(boolean success, String question, String message) {
        this.success = success; this.question = question; this.message = message;
    }
}
