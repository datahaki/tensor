// code by jph
package ch.alpine.tensor.usr;

import java.math.BigDecimal;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.opt.lp.LinearProgram;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;
import ch.alpine.tensor.opt.lp.LinearProgramming;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.HypergeometricDistribution;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ enum ReadmeDemo {
  ;
  static void demoInverse() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 2, -3, 2 }, { 4, 9, -3 }, { -1, 3, 2 } });
    System.out.println(Pretty.of(matrix));
    System.out.println(Pretty.of(Inverse.of(matrix)));
  }

  static void demoPseudoInverse() {
    Tensor matrix = Tensors.fromString("{{-1 + I, 0}, {-I, 2}, {2 - I, 2 * I}}");
    System.out.println(Pretty.of(PseudoInverse.of(matrix)));
  }

  static void demoNullspace() {
    System.out.println(Pretty.of(NullSpace.of(Tensors.fromString("{{-1/3, 0, I}}"))));
  }

  static void demoSVD() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { 2, -3, 2 }, { 4, 9, -3 }, { -1, 3, 2 } });
    System.out.println(Pretty.of(SingularValueDecomposition.of(matrix).getU().map(Round._4)));
  }

  static void demoLP() {
    LinearProgram linearProgram = LinearProgram.of(Objective.MAX, Tensors.vector(1, 1), ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{4, -1},{2, 1},{-5, 2}}"), //
        Tensors.vector(8, 7, 2), Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    System.out.println(x);
  }

  static void demoSqrt() {
    Scalar fraction = RationalScalar.of(-9, 16);
    System.out.println(Sqrt.FUNCTION.apply(fraction));
  }

  static void demoPDF() {
    Distribution distribution = HypergeometricDistribution.of(10, 50, 100);
    System.out.println(RandomVariate.of(distribution, 20));
    // ---
    PDF pdf = PDF.of(distribution);
    System.out.println("P(X=3)=" + pdf.at(RealScalar.of(3)));
  }

  static void demoCross() {
    Tensor ad = LeviCivitaTensor.of(3);
    Tensor x = Tensors.vector(7, 2, -4);
    Tensor y = Tensors.vector(-3, 5, 2);
    System.out.println(ad);
    System.out.println(ad.dot(x).dot(y));
  }

  static void demoAssign() {
    Tensor matrix = Array.zeros(3, 4);
    matrix.set(Tensors.vector(9, 8, 4, 5), 2);
    matrix.set(Tensors.vector(6, 7, 8), Tensor.ALL, 1);
    System.out.println(Pretty.of(matrix));
    System.out.println(matrix.get(Tensor.ALL, 3));
  }

  static void demoDecimal() {
    System.out.println(Exp.FUNCTION.apply(DecimalScalar.of(new BigDecimal("10"))));
    System.out.println(Sqrt.FUNCTION.apply(DecimalScalar.of(new BigDecimal("2"))));
    Scalar a = N.in(100).of(RealScalar.of(2));
    System.out.println(Sqrt.FUNCTION.apply(a));
  }

  public static void main(String[] args) {
    demoLP();
    // demoPseudoInverse();
    // demoSqrt();
    // demoInverse();
    // demoPDF();
    // demoSVD();
    // demoAssign();
    // demoDecimal();
  }
}
