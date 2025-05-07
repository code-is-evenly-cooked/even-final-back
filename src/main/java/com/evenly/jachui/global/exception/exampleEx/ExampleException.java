package com.evenly.jachui.global.exception.exampleEx;

// 예시 예외
public class ExampleException extends RuntimeException {
    public ExampleException(String message) {
        super(message);
    }

    public static ExampleException NotFoundExampleException() {
      return new ExampleException("예시 예외를 찾을 수 없습니다.");
    }
}
