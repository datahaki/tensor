// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class TimesTest {
  /** The return value has {@link Dimensions} of input tensor reduced by 1.
   * 
   * <p>For instance
   * <pre>
   * pmul({ 3, 4, 2 }) == 3 * 4 * 2 == 24
   * pmul({ { 1, 2, 3 }, { 4, 5, 6 } }) == { 4, 10, 18 }
   * </pre>
   * 
   * <p>For an empty list, the result is RealScalar.ONE. This is consistent with
   * Mathematica::Times @@ {} == 1
   * 
   * <p>implementation is consistent with MATLAB::prod
   * pmul([]) == 1
   * 
   * @param tensor
   * @return total pointwise product of tensor entries at first level, or 1 if tensor is empty
   * @throws TensorRuntimeException if input tensor is a scalar */
  private static Tensor pmul(Tensor tensor) {
    return Times.of(tensor.stream().toArray(Tensor[]::new));
  }

  @Test
  public void testSingle() {
    assertEquals(Times.of(RealScalar.of(3)), RealScalar.of(3));
  }

  @Test
  public void testProduct() {
    assertEquals(RealScalar.of(3).multiply(RealScalar.of(8)), RealScalar.of(3 * 8));
    assertEquals(Times.of(RealScalar.of(3), RealScalar.of(8), RealScalar.of(-4)), RealScalar.of(3 * 8 * -4));
  }

  @Test
  public void testEmpty() {
    assertEquals(Times.of(), RealScalar.ONE);
  }

  @Test
  public void testGaussScalar() {
    GaussScalar a = GaussScalar.of(3, 7);
    GaussScalar b = GaussScalar.of(4, 7);
    assertEquals(a.multiply(b), GaussScalar.of(12, 7));
  }

  @Test
  public void testPmulEmpty() {
    Tensor a = Tensors.of(Tensors.empty());
    Tensor b = pmul(a);
    assertEquals(b, Tensors.empty());
    assertEquals(RealScalar.of(1), pmul(Tensors.empty()));
  }

  @Test
  public void testPmul1() {
    Tensor a = Tensors.vectorLong(1, 2, 2, 5, 1);
    Tensor r = pmul(a);
    assertEquals(r, RealScalar.of(20));
  }

  @Test
  public void testPmul2() {
    Tensor a = Tensors.matrix(new Number[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } });
    Tensor r = pmul(a);
    assertEquals(r, Tensors.vector(15, 48));
    assertEquals(Dimensions.of(r), Lists.rest(Dimensions.of(a)));
  }

  @Test
  public void testPmul3() {
    Tensor a = Tensors.matrix(new Number[][] { { 1., 2, 3 }, { 4, 5., 6 } });
    Tensor r = pmul(a);
    assertEquals(r, Tensors.vector(4, 10, 18));
  }

  @Test
  public void testVectorMatrixEx1() {
    Tensor mat = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}");
    Times.of(Tensors.vector(-2, 2), mat);
    Tensor factor = Tensors.vector(-2, 2, 2);
    Tensor rep = TensorMap.of(Times.operator(factor), mat, 1);
    assertEquals(rep, Tensors.fromString("{{-2, 4, 6}, {-8, 10, 12}}"));
  }

  @Test
  public void testVectorMatrixEx2() {
    Tensor a = Tensors.of( //
        Tensors.vectorLong(1, 2, 3), //
        Tensors.vectorLong(3, -1, -1));
    Tensor b = Tensors.vectorLong(3, 2, 2);
    Tensor c = Tensors.of( //
        Tensors.vectorLong(3, 4, 6), //
        Tensors.vectorLong(9, -2, -2));
    Tensor r = Tensor.of(a.flatten(0).map(row -> Times.of(row, b)));
    assertEquals(r, c);
  }

  @Test
  public void testVectorMatrix() {
    Tensor a = Tensors.of( //
        Tensors.vectorLong(new long[] { 1, 2, 3 }), //
        Tensors.fromString("{3,-1,-1}"));
    Tensor b = Tensors.vectorLong(3, 2);
    Tensor c = Tensors.of( //
        Tensors.vectorLong(3, 6, 9), //
        Tensors.vectorLong(6, -2, -2));
    assertEquals(Times.of(b, a), c);
  }

  @Test
  public void testMatrixMatrix() {
    Tensor a = Tensors.of( //
        Tensors.vectorLong(1, 2, 3), //
        Tensors.vectorLong(3, -1, -1));
    Tensor c = Tensors.of( //
        Tensors.vectorLong(3, 4, 6), //
        Tensors.vectorLong(-9, -2, -2));
    Tensor r = Tensors.fromString("{{3, 8, 18}, {-27, 2, 2}}");
    assertEquals(Times.of(a, c), r);
  }

  @Test
  public void testChain() {
    Tensor tensor = Times.of( //
        Tensors.fromString("{1,2,3}"), //
        Tensors.fromString("{1,2,3}"), //
        Tensors.fromString("{1[s],2[m],3[N]}"), //
        Tensors.fromString("{5,1/2,1/9}"));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{5[s], 4[m], 3[N]}"));
  }

  @Test
  public void testSingleCopy() {
    Tensor tensor = Tensors.fromString("{1,2,3}");
    Tensor result = Times.of(tensor);
    tensor.set(RealScalar.of(0), 0);
    assertEquals(tensor, Tensors.vector(0, 2, 3));
    assertEquals(result, Tensors.vector(1, 2, 3));
  }

  @Test
  public void testDiagonalMatrix() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Tensor v = RandomVariate.of(distribution, 3);
    Tensor m = RandomVariate.of(distribution, 3, 4, 2);
    assertEquals(DiagonalMatrix.with(v).dot(m), Times.of(v, m));
  }

  @Test
  public void testOperator() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = Times.operator(Tensors.vector(1, 2, 3));
    Serialization.copy(tuo).apply(Tensors.vector(3, 4, 5));
  }

  @Test
  public void testFail() {
    assertThrows(IllegalArgumentException.class, () -> Times.of(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  public void testTotalProdFail() {
    assertThrows(TensorRuntimeException.class, () -> pmul(RealScalar.ONE));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> Times.of(RealScalar.of(3), null, RealScalar.of(8)));
  }
}
