// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Mod;

/* package */ class ExtendedGCDWrap implements Serializable {
  // TODO TENSOR API is dubious
  public ExtendedGCD function(Tensor vector) {
    return new ExtendedGCDImpl(vector);
  }

  private static class ExtendedGCDImpl implements ExtendedGCD, Serializable {
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
      return MathematicaFormat.concise("ExtendedGCD", a, x, b, y, gcd);
    }
  }
}
