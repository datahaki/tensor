// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Mod;

/* package */ class ExtendedGCDWrap implements Serializable {
  public ExtendedGCD function(Tensor vector) {
    return new ExtendedGCDImpl(vector);
  }

  private class ExtendedGCDImpl implements ExtendedGCD, Serializable {
    private final Scalar a;
    private final Scalar b;
    private final Scalar x;
    private final Scalar y;
    private final Scalar gcd;

    public ExtendedGCDImpl(Tensor vector) {
      VectorQ.requireLength(vector, 2);
      this.a = vector.Get(0);
      this.b = vector.Get(1);
      if (Scalars.isZero(a)) {
        x = a;
        y = a.one();
        gcd = b;
      } else {
        ExtendedGCDImpl result = new ExtendedGCDImpl(Tensors.of(Mod.function(a).apply(b), a));
        x = result.y.subtract(Floor.FUNCTION.apply(b.divide(a)).multiply(result.x));
        y = result.x;
        gcd = result.gcd;
      }
    }

    @Override // from ExtendedGCD
    public Scalar gcd() {
      return gcd;
    }

    @Override // from ExtendedGCD
    public Tensor factors() {
      return Tensors.of(x, y);
    }

    @Override
    public String toString() {
      return String.format("%s[%s*%s+%s*%s==%s]", ExtendedGCD.class.getSimpleName(), a, x, b, y, gcd);
    }
  }
}
