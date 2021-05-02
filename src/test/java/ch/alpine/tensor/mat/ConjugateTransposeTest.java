// code by jph
package ch.alpine.tensor.mat;

import java.util.Arrays;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ConjugateTransposeTest extends TestCase {
  public void testExample1() {
    Tensor m1 = Tensors.fromString("{{1, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  public void testExample2() {
    Tensor m1 = Tensors.fromString("{{1+2*I, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1-2*I, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  public void testRank3() {
    Tensor tensor = ConjugateTranspose.of(RandomVariate.of(UniformDistribution.unit(), 2, 3, 4));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 2, 4));
  }

  public void testScalarFail() {
    AssertFail.of(() -> ConjugateTranspose.of(RealScalar.ONE));
  }

  public void testVectorFail() {
    AssertFail.of(() -> ConjugateTranspose.of(Tensors.vector(1, 2, 3)));
  }
}
