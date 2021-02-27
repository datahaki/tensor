// code by jph
package ch.ethz.idsc.tensor.nrm;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.LogisticDistribution;
import ch.ethz.idsc.tensor.pdf.NegativeBinomialDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.red.Projection;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Conjugate;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormalizeTest extends TestCase {
  // function requires that vector != 0
  private static void _checkNormalize(Tensor vector, TensorScalarFunction tsf) {
    Scalar value = tsf.apply(Normalize.with(tsf).apply(vector));
    Chop._13.requireClose(value, RealScalar.ONE);
    Chop._13.requireClose(tsf.apply(NormalizeUnlessZero.with(tsf).apply(vector)), RealScalar.ONE);
  }

  private static void _checkNormalizeAllNorms(Tensor vector) {
    _checkNormalize(vector, Vector1Norm::of);
    _checkNormalize(vector, VectorInfinityNorm::of);
    _checkNormalize(vector, Vector2Norm::of);
  }

  public void testVector1() {
    Tensor vector = Tensors.vector(3, 3, 3, 3);
    Tensor n = Vector2Norm.NORMALIZE.apply(vector);
    assertEquals(n.toString(), "{1/2, 1/2, 1/2, 1/2}");
    _checkNormalizeAllNorms(vector);
    _checkNormalizeAllNorms(Tensors.vector(3, 2, 1));
  }

  public void testVectorNumeric() {
    Distribution distribution = LogisticDistribution.of(1, 100);
    _checkNormalizeAllNorms(RandomVariate.of(distribution, 1000));
  }

  public void testVectorExact() {
    Distribution distribution = NegativeBinomialDistribution.of(3, RationalScalar.of(2, 3));
    _checkNormalizeAllNorms(RandomVariate.of(distribution, 1000));
  }

  public void testNorm1Documentation() {
    Tensor vector = Tensors.vector(2, -3, 1);
    Tensor result = Normalize.with(Vector1Norm::of).apply(vector);
    assertEquals(result, Tensors.fromString("{1/3, -1/2, 1/6}"));
    ExactTensorQ.require(result);
  }

  public void testNormInfinityDocumentation() {
    Tensor vector = Tensors.vector(2, -3, 1);
    Tensor result = Normalize.with(VectorInfinityNorm::of).apply(vector);
    assertEquals(result, Tensors.fromString("{2/3, -1, 1/3}"));
    ExactTensorQ.require(result);
  }

  public void testEps() {
    Tensor vector = Tensors.vector(0, Double.MIN_VALUE, 0);
    assertEquals(Normalize.with(Vector1Norm::of).apply(vector), Tensors.vector(0, 1, 0));
    assertEquals(Normalize.with(Vector2Norm::of).apply(vector), Tensors.vector(0, 1, 0));
    assertEquals(Normalize.with(VectorInfinityNorm::of).apply(vector), Tensors.vector(0, 1, 0));
  }

  public void testEps2() {
    _checkNormalizeAllNorms(Tensors.vector(0, -Double.MIN_VALUE, 0, 0, 0));
    _checkNormalizeAllNorms(Tensors.vector(0, 0, Double.MIN_VALUE));
    _checkNormalizeAllNorms(Tensors.vector(0, Double.MIN_VALUE, Double.MIN_VALUE));
    _checkNormalizeAllNorms(Tensors.vector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
  }

  public void testNorm1() {
    Tensor v = Tensors.vector(1, 1, 1);
    Tensor n = Normalize.with(Vector1Norm::of).apply(v);
    assertEquals(n, Tensors.fromString("{1/3, 1/3, 1/3}"));
    _checkNormalizeAllNorms(v);
  }

  public void testNormInf() {
    Tensor d = Tensors.vector(1, 1, 1).multiply(RealScalar.of(2));
    Tensor n = Normalize.with(VectorInfinityNorm::of).apply(d);
    assertEquals(n, Tensors.vector(1, 1, 1));
    ExactTensorQ.require(n);
    _checkNormalizeAllNorms(d);
    _checkNormalizeAllNorms(n);
  }

  public void testComplex() throws ClassNotFoundException, IOException {
    TensorUnaryOperator normalize = Serialization.copy(Vector2Norm.NORMALIZE);
    Tensor vector = Tensors.fromString("{1+I, 2*I, -3-9.2*I}");
    Tensor s = normalize.apply(vector);
    Chop._13.requireClose(s.dot(Conjugate.of(s)), RealScalar.ONE);
    Chop._13.requireClose(Conjugate.of(s).dot(s), RealScalar.ONE);
  }

  public void testComplex2() {
    Tensor vector = Tensors.fromString("{3*I, 4}");
    Tensor s = Vector2Norm.NORMALIZE.apply(vector);
    assertEquals(Projection.on(vector).apply(s), s);
    assertEquals(Projection.on(s).apply(vector), vector);
    Chop._13.requireClose(s.dot(Conjugate.of(s)), RealScalar.ONE);
    Chop._13.requireClose(Conjugate.of(s).dot(s), RealScalar.ONE);
  }

  public void testQuantityTensor() {
    Tensor vector = QuantityTensor.of(Tensors.vector(2, 3, 4), "m*s^-1");
    _checkNormalizeAllNorms(vector);
  }

  public void testNormalizeTotal() {
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(Total::ofVector);
    assertTrue(tensorUnaryOperator.toString().startsWith("Normalize"));
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(-1, 3, 2));
    assertEquals(tensor, Tensors.fromString("{-1/4, 3/4, 1/2}"));
    AssertFail.of(() -> tensorUnaryOperator.apply(Tensors.empty()));
  }

  public void testInconsistentFail() {
    Distribution distribution = UniformDistribution.of(3, 5);
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(v -> RandomVariate.of(distribution));
    AssertFail.of(() -> tensorUnaryOperator.apply(Tensors.vector(-1, 3, 2)));
  }

  public void testNormalizeTotalFail() {
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(Total::ofVector);
    AssertFail.of(() -> tensorUnaryOperator.apply(Tensors.vector(-1, 3, -2)));
  }

  public void testNormalizeNullFail() {
    TensorScalarFunction tensorScalarFunction = null;
    AssertFail.of(() -> Normalize.with(tensorScalarFunction));
  }
}
