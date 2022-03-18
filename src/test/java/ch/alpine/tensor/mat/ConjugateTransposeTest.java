// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;

public class ConjugateTransposeTest {
  @Test
  public void testExample1() {
    Tensor m1 = Tensors.fromString("{{1, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  @Test
  public void testExample2() {
    Tensor m1 = Tensors.fromString("{{1+2*I, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1-2*I, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  @Test
  public void testRank3() {
    Tensor tensor = ConjugateTranspose.of(RandomVariate.of(UniformDistribution.unit(), 2, 3, 4));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 2, 4));
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> ConjugateTranspose.of(RealScalar.ONE));
  }

  @Test
  public void testVectorFail() {
    AssertFail.of(() -> ConjugateTranspose.of(Tensors.vector(1, 2, 3)));
  }
}
