// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;

class ProjectionTest {
  @Test
  void testReal() throws ClassNotFoundException, IOException {
    Tensor projection = Serialization.copy(Projection.on(Tensors.vector(1, 0, 0))).apply(Tensors.vector(1, 1, 1));
    assertEquals(projection, UnitVector.of(3, 0));
    Tensor p2 = Projection.on(Tensors.vector(1, 1, 1)).apply(Tensors.vector(5, 6, 7));
    assertEquals(p2, Tensors.vector(6, 6, 6));
    ExactTensorQ.require(p2);
  }

  @Test
  void testComplex() {
    TensorUnaryOperator tensorUnaryOperator = Projection.on(Tensors.vector(1, 1, 1));
    Tensor p2 = tensorUnaryOperator.apply(Tensors.fromString("{5, I, 7}"));
    assertEquals(Tensors.fromString("{4 + I/3, 4 + I/3, 4 + I/3}"), p2);
    ExactTensorQ.require(p2);
    assertThrows(ClassCastException.class, () -> tensorUnaryOperator.apply(HilbertMatrix.of(3, 3)));
  }

  @Test
  void testUV() {
    Tensor u = Tensors.fromString("{1 + I, 3 - 2*I}");
    Tensor v = Tensors.fromString("{2 - 4*I, 1 + 7*I}");
    Tensor m_uv = Tensors.fromString("{-(47/35) + (9*I)/35, 53/35 - (54*I)/35}");
    Tensor uv = Projection.of(u, v);
    assertEquals(uv, m_uv);
    ExactTensorQ.require(uv);
    Tensor pOnV_u = Projection.on(v).apply(u);
    assertEquals(pOnV_u, uv);
    ExactTensorQ.require(pOnV_u);
  }

  @Test
  void testVU() {
    Tensor u = Tensors.fromString("{1 + I, 3 - 2*I}");
    Tensor v = Tensors.fromString("{2 - 4*I, 1 + 7*I}");
    Tensor m_vu = Tensors.fromString("{-2 + (4*I)/15, -(1/3) + (77*I)/15}");
    Tensor vu = Projection.of(v, u);
    assertEquals(vu, m_vu);
    ExactTensorQ.require(vu);
    Tensor pOnU_v = Projection.on(u).apply(v);
    assertEquals(pOnU_v, vu);
    ExactTensorQ.require(pOnU_v);
  }

  @Test
  void testZeroFail() {
    assertThrows(Throw.class, () -> Projection.on(Tensors.vector(0, 0, 0)));
    assertThrows(Throw.class, () -> Projection.on(Tensors.vector(0.0, 0, 0)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Projection.on(RealScalar.ONE));
  }

  @Test
  void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> Projection.on(HilbertMatrix.of(2, 2)));
    assertThrows(IllegalArgumentException.class, () -> Projection.on(HilbertMatrix.of(3, 2)));
  }
}
