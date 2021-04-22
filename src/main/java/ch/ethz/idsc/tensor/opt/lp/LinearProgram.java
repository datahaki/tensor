// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** definition of a linear program
 * 
 * Example:
 * <pre>
 * LinearProgram.of( //
 * Objective.MAX, Tensors.vector(3, 5), //
 * ConstraintType.LESS_EQUALS, //
 * Tensors.matrixInt(new int[][] { { 1, 5 }, { 2, 1 }, { 1, 1 } }), //
 * Tensors.vector(40, 20, 12), RegionType.NON_NEGATIVE);
 * </pre>
 * 
 * @see LinearProgramming */
public class LinearProgram implements Serializable {
  /** @param objective
   * @param c
   * @param constraintType
   * @param A
   * @param b
   * @param regionType
   * @return */
  public static LinearProgram of( //
      Objective objective, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      RegionType regionType) {
    return new LinearProgram( //
        Objects.requireNonNull(objective), //
        VectorQ.requireLength(c, Unprotect.dimension1Hint(A)), //
        Objects.requireNonNull(constraintType), //
        MatrixQ.require(A), //
        VectorQ.requireLength(b, A.length()), //
        Objects.requireNonNull(regionType), //
        c.length());
  }

  public static enum Objective {
    MIN, MAX;

    public Objective flip() {
      return values()[1 - ordinal()];
    }
  }

  public static enum ConstraintType {
    EQUALS, LESS_EQUALS, GREATER_EQUALS;

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

  public static enum RegionType {
    NON_NEGATIVE, COMPLETE;
  }

  /***************************************************/
  public final Objective objective;
  public final Tensor c;
  public final ConstraintType constraintType;
  public final Tensor A;
  public final Tensor b;
  public final RegionType regionType;
  public final int variables;

  private LinearProgram( //
      Objective objective, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      RegionType regionType, int variables) {
    this.objective = objective;
    this.c = c;
    this.constraintType = constraintType;
    this.A = A;
    this.b = b;
    this.regionType = regionType;
    this.variables = variables;
  }

  /** @return linear program dual to this
   * @throws Exception if constraint type is equality */
  public LinearProgram dual() {
    if (!regionType.equals(RegionType.NON_NEGATIVE))
      throw new RuntimeException();
    return new LinearProgram( //
        objective.flip(), //
        b, //
        constraintType.flipInequality(), //
        Transpose.of(A), //
        c, //
        regionType, //
        b.length());
  }

  /** @return */
  public LinearProgram equality() {
    if (constraintType.equals(ConstraintType.EQUALS))
      return this;
    int m = A.length();
    Tensor eye = IdentityMatrix.of(m);
    if (constraintType.equals(ConstraintType.GREATER_EQUALS))
      eye = eye.negate();
    return new LinearProgram( //
        objective, //
        Join.of(c, Array.zeros(m)), //
        ConstraintType.EQUALS, //
        Join.of(1, A, eye), b, //
        regionType, variables);
  }

  public Tensor minObjective() {
    return objective.equals(Objective.MIN) //
        ? c
        : c.negate();
  }

  public Tensor requireFeasible(Tensor x) {
    if (regionType.equals(RegionType.NON_NEGATIVE) && //
        !StaticHelper.isNonNegative(x))
      throw TensorRuntimeException.of(c, A, b, x);
    if (constraintType.equals(ConstraintType.LESS_EQUALS) && //
        !StaticHelper.isNonNegative(b.subtract(A.dot(x))))
      throw TensorRuntimeException.of(c, A, b, x);
    if (constraintType.equals(ConstraintType.GREATER_EQUALS) && //
        !StaticHelper.isNonNegative(A.dot(x).subtract(b)))
      throw TensorRuntimeException.of(c, A, b, x);
    return x;
  }
}
