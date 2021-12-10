// code by jph
package ch.alpine.tensor.ext;

public enum Booleans {
  ;
  public static void requireEquals(Object b1, Object b2) {
    if (!b1.equals(b2)) {
      System.out.println("---");
      System.out.println(b1);
      System.out.println(b2);
      throw new RuntimeException();
    }
  }
}
