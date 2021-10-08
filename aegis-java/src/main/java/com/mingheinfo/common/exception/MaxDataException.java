package com.mingheinfo.common.exception;

import lombok.Data;

@Data
public class MaxDataException extends RuntimeException {

    private String msg;

    public MaxDataException(String msg){
        super(msg);
        this.msg = msg;
    }
}
