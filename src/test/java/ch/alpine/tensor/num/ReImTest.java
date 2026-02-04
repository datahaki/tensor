// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

class ReImTest {
  @Test
  void testSimple() {
    assertEquals(ReIm.of(RealScalar.ONE).vector(), UnitVector.of(2, 0));
    assertEquals(ReIm.of(ComplexScalar.I).vector(), UnitVector.of(2, 1));
    assertEquals(ReIm.of(ComplexScalar.of(3, 4)).vector(), Tensors.vector(3, 4));
  }

  @Test
  void testRotate() {
    ReIm reIm = ReIm.product(RealScalar.of(3), RealScalar.of(4), RealScalar.of(7), RealScalar.of(11));
    Scalar scalar = Scalars.fromString("(3+4*I)*(7+11*I)");
    assertEquals(reIm.re(), Re.FUNCTION.apply(scalar));
    assertEquals(reIm.im(), Im.FUNCTION.apply(scalar));
    ExactScalarQ.require(reIm.re());
    ExactScalarQ.require(reIm.im());
  }

  @Test
  void testProd() {
    Scalar z = ComplexScalar.of(2, 3);
    Scalar a = ComplexScalar.of(5, 11);
    Tensor vector = Tensors.vector(5, 11);
    Tensor tensor = ReIm.of(z).rotate(vector);
    assertEquals(tensor, Tensors.vector(-23, 37));
    ExactTensorQ.require(tensor);
    Scalar compare = z.multiply(a);
    assertEquals(compare, ComplexScalar.of(-23, 37));
  }

  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    ReIm reIm = ReIm.of(ComplexScalar.of(2, 5));
    Serialization.copy(reIm);
  }
}
