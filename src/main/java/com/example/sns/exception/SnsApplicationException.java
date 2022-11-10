package com.example.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SnsApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;


    // 에러코드만 있는 경우
    public SnsApplicationException(ErrorCode errorCode){
     this.errorCode = errorCode;
     this.message = null;
    }


    // 반환할 에러 메시지가 있는 경우를 위한 오버라이딩
    @Override
    public String getMessage() {

        if(message == null){
            return errorCode.getMessage();
        }

        return String.format("%s. %s", errorCode.getMessage(), message);
    }

}
