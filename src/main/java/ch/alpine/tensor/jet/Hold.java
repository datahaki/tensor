// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

/** EXPERIMENTAL */
public enum Hold {
  ;
  private static class Zero extends AbstractScalar implements Serializable {
    @Override
    public Scalar multiply(Scalar scalar) {
      return scalar.zero();
    }

    @Override
    public Scalar negate() {
      return this;
    }

    @Override
    public Scalar reciprocal() {
      throw new Throw(this);
    }

    @Override
    public Scalar zero() {
      return this;
    }

    @Override
    public Scalar one() {
      throw new Throw(this);
    }

    @Override
    public Number number() {
      return RealScalar.ZERO.number();
    }

    @Override
    protected Scalar plus(Scalar scalar) {
      return scalar;
    }

    @Override
    public int hashCode() {
      return 42;
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof Zero;
    }

    @Override
    public String toString() {
      return RealScalar.ZERO.toString();
    }
  }

  private static final Scalar ZERO = new Zero();

  public static Scalar zero() {
    return ZERO;
  }
}
