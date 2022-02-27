// code by jph
package ch.alpine.tensor.lie;

import java.util.Arrays;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CrossTest extends TestCase {
  private static final Tensor SO3 = LeviCivitaTensor.of(3).negate();

  public static Tensor alt_skew3(Tensor a) {
    return SO3.dot(a);
  }

  static void checkAB(Tensor a, Tensor b) {
    Tensor r1 = Cross.of(a, b);
    Tensor r2 = Cross.skew3(a).dot(b);
    Tensor r3 = alt_skew3(a).dot(b);
    assertEquals(r1, r2);
    assertEquals(r2, r3);
  }

  public void testUnits() {
    Tensor v1 = UnitVector.of(3, 0);
    Tensor v2 = UnitVector.of(3, 1);
    Tensor v3 = UnitVector.of(3, 2);
    ExactTensorQ.require(Cross.of(v1, v2));
    assertEquals(Cross.of(v1, v2), v3);
    ExactTensorQ.require(Cross.of(v2, v3));
    assertEquals(Cross.of(v2, v3), v1);
    ExactTensorQ.require(Cross.of(v3, v1));
    assertEquals(Cross.of(v3, v1), v2);
  }

  public void testNormal() {
    Distribution distribution = NormalDistribution.standard();
    for (int c = 0; c < 10; ++c) {
      Tensor a = RandomVariate.of(distribution, 3);
      Tensor b = RandomVariate.of(distribution, 3);
      checkAB(a, b);
    }
  }

  public void testUniform() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int c = 0; c < 10; ++c) {
      Tensor a = RandomVariate.of(distribution, 3);
      Tensor b = RandomVariate.of(distribution, 3);
      checkAB(a, b);
    }
  }

  public void testGauss() {
    Tensor v1 = Tensors.of( //
        GaussScalar.of(3, 7), //
        GaussScalar.of(4, 7), //
        GaussScalar.of(2, 7)); //
    Tensor v2 = Tensors.of( //
        GaussScalar.of(1, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(6, 7)); //
    Tensor tensor = Cross.of(v1, v2);
    Tensor v3 = Tensors.of( //
        GaussScalar.of(0, 7), //
        GaussScalar.of(5, 7), //
        GaussScalar.of(4, 7)); //
    assertEquals(tensor, v3);
  }

  public void testSkew3LengthFail() {
    AssertFail.of(() -> Cross.skew3(Tensors.vector(1, 2, 3, 4)));
  }

  public void testFailLength2() {
    Tensor v1 = UnitVector.of(3, 0);
    Tensor v2 = UnitVector.of(2, 1);
    AssertFail.of(() -> Cross.of(v1, v2));
    AssertFail.of(() -> Cross.of(v2, v1));
  }

  public void testFailLength4() {
    Tensor v1 = UnitVector.of(4, 0);
    Tensor v2 = UnitVector.of(3, 1);
    AssertFail.of(() -> Cross.of(v1, v2));
    AssertFail.of(() -> Cross.of(v2, v1));
  }

  public void test2DSimple() {
    // Cross[{1, 2}] == {-2, 1}
    assertEquals(Cross.of(Tensors.vector(1, 2)), Tensors.vector(-2, 1));
  }

  public void test2DRotation() {
    Tensor x = Tensors.vector(1, 2);
    Tensor mat = Tensors.fromString("{{0, -1}, {1, 0}}");
    assertEquals(Cross.of(x), mat.dot(x));
  }

  public void test2DApply() {
    Tensor tensor = Tensor.of(HilbertMatrix.of(10, 2).stream().map(Cross::of));
    assertEquals(Dimensions.of(tensor), Arrays.asList(10, 2));
  }

  public void test2DFail() {
    AssertFail.of(() -> Cross.of(HilbertMatrix.of(2)));
  }

  public void test2DFail2() {
    AssertFail.of(() -> Cross.of(Tensors.vector(1, 2, 3)));
  }

  public void test2DFailNull() {
    AssertFail.of(() -> Cross.of(null));
  }
}
