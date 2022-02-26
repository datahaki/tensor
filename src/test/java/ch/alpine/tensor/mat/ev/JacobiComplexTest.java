// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.red.Entrywise;
import junit.framework.TestCase;

public class JacobiComplexTest extends TestCase {
  public void testHermitian() {
    Random random = new Random(2);
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = 4;
    Tensor real = Symmetrize.of(RandomVariate.of(distribution, random, n, n));
    Tensor imag = TensorWedge.of(RandomVariate.of(distribution, random, n, n));
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(real, imag);
    HermitianMatrixQ.require(matrix);
    // System.out.println(Diagonal.of(matrix));
    JacobiComplex jacobiComplex = new JacobiComplex(matrix, Tolerance.CHOP);
    Tensor h = jacobiComplex.h();
    // System.out.println(Pretty.of(matrix.map(Round._4)));
    // System.out.println(Pretty.of(h.map(Round._4)));
    Tolerance.CHOP.requireClose( //
        BasisTransform.ofMatrix(h, jacobiComplex.vectors()), //
        matrix);
  }
}
