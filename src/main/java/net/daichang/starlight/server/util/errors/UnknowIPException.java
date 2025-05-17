package net.daichang.starlight.server.util.errors;

public class UnknowIPException extends RuntimeException {
  public UnknowIPException() {
    super("未知的IP");
  }

  @Override
  public String getMessage() {
    return "这个ip是不正确的，无法验证通过的";
  }

  @Override
  public synchronized Throwable getCause() {
    return super.getCause();
  }
}
