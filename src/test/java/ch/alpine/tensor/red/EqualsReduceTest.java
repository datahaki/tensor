// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.qty.Quantity;

class EqualsReduceTest {
  @Test
  void testNumeric() {
    assertInstanceOf(RationalScalar.class, EqualsReduce.zero(Tensors.fromString("{0,0.0,0.0,0}")));
    assertInstanceOf(RationalScalar.class, EqualsReduce.zero(Tensors.fromString("{0,0,0.0,0}")));
    assertInstanceOf(RationalScalar.class, EqualsReduce.zero(Tensors.fromString("{0,0,0,0}")));
  }

  @Test
  void testGetUnitUnique() {
    assertEquals(EqualsReduce.zero(Range.of(1, 10)), RealScalar.ZERO);
    assertEquals(EqualsReduce.zero(Tensors.vector(2, 3, 4)), RealScalar.ZERO);
    assertEquals(EqualsReduce.zero(Tensors.fromString("{{1[m],2+I[m],3[m]}}")), Quantity.of(0, "m"));
    assertEquals(EqualsReduce.zero(DiagonalMatrix.of(3, Quantity.of(1, "s^2*m^-1"))), Quantity.of(0, "s^2*m^-1"));
  }
  // @Test
  // void testFailUnique() {
  // assertThrows(Exception.class, () -> Tensors.vector(1, 2, 3).stream() //
  // .map(Scalar.class::cast).reduce(EqualsReduce.INSTANCE).orElseThrow());
  // }

  @Test
  void testGetUnitUniqueFail1() {
    assertThrows(Exception.class, () -> EqualsReduce.zero(Tensors.fromString("{{1[m],2[s],3[m]}}")));
  }

  @Test
  void testGetUnitUniqueFail2() {
    Tensor tensor = Tensors.fromString("{1[m], 2[s]}");
    assertFalse(StringScalarQ.any(tensor));
    assertThrows(Exception.class, () -> EqualsReduce.zero(tensor));
  }
}
