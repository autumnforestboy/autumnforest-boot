package io.github.autumnforest.boot.commons;

import lombok.Data;

@Data
public class Result<T> {
    public static final String CODE_OK = "200";
    public static final String CODE_ERR = "500";
    public static final String CODE_NOT_FOUNT = "404";
    public static final String CODE_NONE_ACCESS = "403";
    public static final String CODE_NOT_LOGIN = "403";
    private String code;
    private String errMsg;
    private T data;

    private Result(String code, String errMsg, T data) {
        this.code = code;
        this.errMsg = errMsg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(CODE_OK, null, data);
    }

    /**
     * 失败
     *
     * @param code   失败码
     * @param errMsg 失败信息
     * @return r
     */
    public static <T> Result<T> fail(String code, String errMsg) {
        return new Result<T>(code, errMsg, null);
    }

    /**
     * 失败
     *
     * @return r
     */
    public static <T> Result<T> fail() {
        return new Result<T>(CODE_ERR, "请求异常", null);
    }

}

