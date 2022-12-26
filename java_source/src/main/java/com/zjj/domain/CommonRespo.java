package com.zjj.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CommonRespo<T> implements Serializable {
    private String code;
    private String msg ;
    private T object;

}
