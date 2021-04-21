// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class LinearProgram {
  public static enum ConstraintType {
    EQUALS, //
    LESS_EQUALS, //
    GREATER_EQUALS, //
    ;

    public ConstraintType flipInequality() {
      switch (this) {
      case GREATER_EQUALS:
        return LESS_EQUALS;
      case LESS_EQUALS:
        return GREATER_EQUALS;
      default:
        throw new RuntimeException();
      }
    }
  }

  public static enum CostType {
    MIN, //
    MAX, //
    ;

    public CostType flip() {
      return values()[1 - ordinal()];
    }
  }

  public static enum RegionType {
    NON_NEGATIVE, //
    COMPLETE, //
    ;
  }

  public static LinearProgram of( //
      CostType costType, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      RegionType regionType) {
    return new LinearProgram( //
        Objects.requireNonNull(costType), //
        VectorQ.require(c), //
        Objects.requireNonNull(constraintType), //
        MatrixQ.require(A), //
        VectorQ.require(b), //
        Objects.requireNonNull(regionType), //
        c.length());
  }

  /***************************************************/
  public final CostType costType;
  public final Tensor c;
  public final ConstraintType constraintType;
  public final Tensor A;
  public final Tensor b;
  public final RegionType regionType;
  public final int variables;

  private LinearProgram( //
      CostType costType, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      RegionType regionType, int variables) {
    this.costType = costType;
    this.c = c;
    this.constraintType = constraintType;
    this.A = A;
    this.b = b;
    this.regionType = regionType;
    this.variables = variables;
  }

  public LinearProgram dual() {
    if (!regionType.equals(RegionType.NON_NEGATIVE))
      throw new RuntimeException();
    return new LinearProgram( //
        costType.flip(), //
        b, //
        constraintType.flipInequality(), //
        Transpose.of(A), c, //
        regionType, b.length());
  }

  public LinearProgram equality() {
    if (constraintType.equals(ConstraintType.EQUALS))
      return this;
    int m = A.length();
    Tensor eye = IdentityMatrix.of(m);
    if (constraintType.equals(ConstraintType.GREATER_EQUALS))
      eye = eye.negate();
    return new LinearProgram( //
        costType, //
        Join.of(c, Array.zeros(m)), //
        ConstraintType.EQUALS, //
        Join.of(1, A, eye), b, //
        regionType, variables);
  }
}
