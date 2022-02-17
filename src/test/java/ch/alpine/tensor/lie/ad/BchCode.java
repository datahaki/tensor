// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Denominator;
import ch.alpine.tensor.num.Numerator;
import ch.alpine.tensor.sca.Sign;

public enum BchCode {
  ;
  public static void main(String[] args) {
    int n = 10;
    BchTrace bchTrace = new BchTrace(n);
    Set<String> set1 = new HashSet<>();
    Set<Scalar> setConst = new LinkedHashSet<>();
    for (int d = 1; d <= n; ++d) {
      System.out.println("// d = " + d);
      int cnt = 0;
      StringBuilder stringBuilder = new StringBuilder();
      for (Entry<String, Scalar> entry : bchTrace.navigableMap.entrySet()) {
        String key = entry.getKey().toLowerCase();
        Scalar value = entry.getValue();
        if (Scalars.nonZero(value) && key.length() == d) {
          for (int l = key.length() - 2; 0 <= l; --l) {
            String sub = key.substring(l, key.length());
            boolean add = set1.add(sub);
            if (add) {
              System.out.println("Tensor " + sub + " = ad" + sub.charAt(0) + ".dot(" + sub.substring(1) + ");");
            }
          }
          String var = "t" + d + "_" + cnt;
          if (cnt == 0)
            stringBuilder.append(var);
          else
            stringBuilder.append(".add(" + var + ")");
          if (!setConst.contains(value)) {
            // Scalar num = Numerator.FUNCTION.apply(value);
            // Scalar den = Denominator.FUNCTION.apply(value);
            // String cns = (Sign.isPositiveOrZero(num) ? "P" + num : "N" + num.negate()) + "_" + den;
            setConst.add(value);
          }
          // Scalar num = Numerator.FUNCTION.apply(value);
          // Scalar den = Denominator.FUNCTION.apply(value);
          System.out.println("Tensor " + var + " = " + key + ".multiply(" + cns(value) + ");");
          ++cnt;
        }
      }
      System.out.println("Tensor t" + d + " = " + stringBuilder + ";");
    }
    System.out.println("// ---");
    for (Scalar value : setConst) {
      Scalar num = Numerator.FUNCTION.apply(value);
      Scalar den = Denominator.FUNCTION.apply(value);
      String cns = (Sign.isPositiveOrZero(num) ? "P" + num : "N" + num.negate()) + "_" + den;
      String rat = "private static final Scalar " + cns + " = RationalScalar.of(" + num + ", " + den + ");";
      System.out.println(rat);
    }
  }

  private static String cns(Scalar value) {
    Scalar num = Numerator.FUNCTION.apply(value);
    Scalar den = Denominator.FUNCTION.apply(value);
    return (Sign.isPositiveOrZero(num) ? "P" + num : "N" + num.negate()) + "_" + den;
  }
}
