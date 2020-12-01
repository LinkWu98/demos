package tool.link.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 响应对象
 *
 * @author Link50
 * @version 1.0
 * @date 2020/12/1 14:44
 */
@Data
@AllArgsConstructor
public class Response<T> {

    private boolean success;

    private T data;

    private String msg;

    public static <T> Response<T> succeed() {

        return new Response<>(true, null, null);

    }

    public static <T> Response<T> succeed(T data) {

        return new Response<>(true, data, null);

    }

    public static <T> Response<T> succeed(String msg) {

        return new Response<>(true, null, msg);

    }

    public static <T> Response<T> succeed(T data, String msg) {

        return new Response<>(true, data, msg);

    }

    public static <T> Response<T> fail() {

        return new Response<>(false, null, null);

    }


    public static <T> Response<T> fail(T data) {

        return new Response<>(false, data, null);

    }

    public static <T> Response<T> fail(String msg) {

        return new Response<>(false, null, msg);

    }

    public static <T> Response<T> fail(T data, String msg) {

        return new Response<>(false, data, msg);

    }

    public Response<T> data(T data) {
        this.setData(data);
        return this;
    }

    public Response<T> msg(String msg) {
        this.setMsg(msg);
        return this;
    }

}
