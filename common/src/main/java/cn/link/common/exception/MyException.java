package cn.link.common.exception;

/**
 * @author Link
 * @version 1.0
 * @date 2020/11/3 18:35
 */
public class MyException extends Exception {

    public MyException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}