// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.Map.Entry;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Denominator;
import ch.alpine.tensor.num.Numerator;

public enum BchTraceDemo {
  ;
  public static void main(String[] args) {
    int n = 4;
    BchTrace bchTrace = new BchTrace(n);
    for (Entry<String, Scalar> entry : bchTrace.navigableMap.entrySet()) {
      String key = entry.getKey();
      if (// Scalars.nonZero(entry.getValue()) &&
      key.length() == n) {
        System.out.println(entry);
      }
    }
    if (false) {
      System.out.println("---");
      int count = 0;
      for (Entry<String, Scalar> entry : bchTrace.navigableMap.entrySet()) {
        String key = entry.getKey();
        Scalar value = entry.getValue();
        if (Scalars.nonZero(value) && key.length() == n) {
          System.out.println(String.format("Tensor a7_%d=Dot.of(%s).multiply(RationalScalar.of(%s,%s));", count, conv(key), //
              Numerator.FUNCTION.apply(value), Denominator.FUNCTION.apply(value) //
          ));
          ++count;
        }
      }
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("a7_0");
      for (int index = 1; index < count; ++index) {
        stringBuilder.append(".add(a7_" + index + ")");
      }
      System.out.println(stringBuilder.toString());
    }
  }

  static String conv(String key) {
    key = key.toLowerCase();
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 0; index < key.length() - 1; ++index) {
      stringBuilder.append("ad" + key.charAt(index) + ", ");
    }
    stringBuilder.append(key.charAt(key.length() - 1));
    return stringBuilder.toString();
  }
}
