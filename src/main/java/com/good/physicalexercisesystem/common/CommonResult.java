package com.good.physicalexercisesystem.common;

import com.good.physicalexercisesystem.common.exception.enums.GlobalErrorCodeConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "success", data);
    }

    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(200, message, data);
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        return new CommonResult<>(code, message, null);
    }

    public static <T> CommonResult<T> error(String message) {
        return new CommonResult<>(400, message, null);
    }

    public static <T> CommonResult<T> error(GlobalErrorCodeConstants enums) {
        return new CommonResult<>(enums.getCode(),enums.getMsg());
    }
}
