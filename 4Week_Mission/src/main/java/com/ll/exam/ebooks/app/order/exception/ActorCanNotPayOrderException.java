package com.ll.exam.ebooks.app.order.exception;

public class ActorCanNotPayOrderException extends RuntimeException {
    public ActorCanNotPayOrderException() {
    }

    public ActorCanNotPayOrderException(String msg) {
        super(msg);
    }
}
