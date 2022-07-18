package ch.alpine.tensor.opt.qh3;

/** Exception thrown when QuickHull3D encounters an internal error. */
public class InternalErrorException extends RuntimeException {
  public InternalErrorException(String msg) {
    super(msg);
  }
}
