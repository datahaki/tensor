// code by jph
package ch.alpine.tensor.lie.bch;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Denominator;
import ch.alpine.tensor.num.Numerator;
import ch.alpine.tensor.sca.Sign;

/** <a href="https://en.wikipedia.org/wiki/Jacobi_identity">Jacobi identity</a> */
public enum BchCode {
  ;
  static void main() {
    int n = 6;
    BchTrace bchTrace = new BchTrace(n);
    Set<String> set1 = new HashSet<>();
    Set<Scalar> setConst = new LinkedHashSet<>();
    IO.println("// d = " + 1);
    IO.println("Tensor t1 = x.add(y);");
    for (int d = 2; d <= n; ++d) {
      IO.println("// d = " + d);
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
              IO.println("Tensor " + sub + " = ad" + sub.charAt(0) + ".dot(" + sub.substring(1) + ");");
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
          if (value.equals(RealScalar.ONE))
            IO.println("Tensor " + var + " = " + key + ";");
          else
            IO.println("Tensor " + var + " = " + key + ".multiply(" + cns(value) + ");");
          ++cnt;
        }
      }
      IO.println("Tensor t" + d + " = " + stringBuilder + ";");
    }
    IO.println("// ---");
    for (Scalar value : setConst)
      if (!value.equals(RealScalar.ONE)) {
        Scalar num = Numerator.FUNCTION.apply(value);
        Scalar den = Denominator.FUNCTION.apply(value);
        String cns = (Sign.isPositiveOrZero(num) ? "P" + num : "N" + num.negate()) + "_" + den;
        String rat = "private static final Scalar " + cns + " = RationalScalar.of(" + num + ", " + den + ");";
        IO.println(rat);
      }
  }

  private static String cns(Scalar value) {
    Scalar num = Numerator.FUNCTION.apply(value);
    Scalar den = Denominator.FUNCTION.apply(value);
    return (Sign.isPositiveOrZero(num) ? "P" + num : "N" + num.negate()) + "_" + den;
  }
}
