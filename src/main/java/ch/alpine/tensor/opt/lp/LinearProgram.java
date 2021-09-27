// code by jph
package ch.alpine.tensor.opt.lp;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.IdentityMatrix;

/** definition of a linear program
 * 
 * The terminology of the implementation and comments are taken from
 * "Linear and Integer Programming made Easy", p. 63
 * by T.C. Hu, Andrew B. Kahng, 2016
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
   * @param variables
   * @return */
  public static LinearProgram of( //
      Objective objective, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      Variables variables) {
    return new LinearProgram( //
        Objects.requireNonNull(objective), //
        VectorQ.requireLength(c, Unprotect.dimension1Hint(A)), //
        Objects.requireNonNull(constraintType), //
        MatrixQ.require(A), //
        VectorQ.requireLength(b, A.length()), //
        Objects.requireNonNull(variables), //
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

  public static enum Variables {
    NON_NEGATIVE, UNRESTRICTED;
  }

  // ---
  public final Objective objective;
  public final Tensor c;
  public final ConstraintType constraintType;
  public final Tensor A;
  public final Tensor b;
  public final Variables variables;
  private final int var_count;

  private LinearProgram( //
      Objective objective, //
      Tensor c, //
      ConstraintType constraintType, //
      Tensor A, //
      Tensor b, //
      Variables variables, //
      int var_count) {
    this.objective = objective;
    this.c = c;
    this.constraintType = constraintType;
    this.A = A;
    this.b = b;
    this.variables = variables;
    this.var_count = var_count;
  }

  /** Theorem 5.1 (Theorem of Duality) Given a pair of primal and dual programs
   * (in canonical form), exactly one of the following cases must be true:
   * 
   * 1) Both programs have optimum solutions and their values are the same,
   * i.e., min z = max w <=> min cx = max yb.
   * 2) One program has no feasible solution, and the other program has at least one
   * feasible solution, but no (finite) optimum solution.
   * 3) Neither of the two programs has a feasible solution.
   * 
   * @return linear program dual to this
   * @throws Exception if constraint type is equality */
  public LinearProgram toggle() {
    if (!variables.equals(Variables.NON_NEGATIVE))
      throw new RuntimeException();
    return new LinearProgram( //
        objective.flip(), //
        b, //
        constraintType.flipInequality(), //
        Transpose.of(A), //
        c, //
        variables, //
        b.length());
  }

  /** @return linear program in standard form */
  public LinearProgram standard() {
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
        variables, //
        var_count);
  }

  public Tensor minObjective() {
    return objective.equals(Objective.MIN) //
        ? c
        : c.negate();
  }

  public int var_count() {
    return var_count;
  }

  public Tensor requireFeasible(Tensor x) {
    if (variables.equals(Variables.NON_NEGATIVE) && //
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

  public boolean isCanonicPrimal() {
    return objective.equals(Objective.MIN) //
        && constraintType.equals(ConstraintType.GREATER_EQUALS) //
        && variables.equals(Variables.NON_NEGATIVE);
  }

  public boolean isCanonicDual() {
    return objective.equals(Objective.MAX) //
        && constraintType.equals(ConstraintType.LESS_EQUALS) //
        && variables.equals(Variables.NON_NEGATIVE);
  }

  public boolean isStandardPrimal() {
    return objective.equals(Objective.MIN) //
        && constraintType.equals(ConstraintType.EQUALS) //
        && variables.equals(Variables.NON_NEGATIVE);
  }

  public boolean isStandardDual() {
    return objective.equals(Objective.MAX) //
        && constraintType.equals(ConstraintType.LESS_EQUALS) //
        && variables.equals(Variables.UNRESTRICTED);
  }
}
