// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.red.Total;

class QuantityAdditiveTest {
  private static void _checkPlusSymmetry(Scalar s1, Scalar s2) {
    Scalar r1 = s1.add(s2);
    Scalar r2 = s2.add(s1);
    assertEquals(r1.toString(), r2.toString());
  }

  @Test
  public void testMixFail() {
    assertThrows(TensorRuntimeException.class, () -> Quantity.of(0, "m").add(Quantity.of(0, "kg")));
    assertThrows(TensorRuntimeException.class, () -> Quantity.of(0, "m").add(Quantity.of(2, "kg")));
    assertThrows(TensorRuntimeException.class, () -> Quantity.of(0, "m").add(Quantity.of(2, "")));
  }

  @Test
  public void testPlusUnits3() {
    Scalar s1 = Quantity.of(0, "m"); //
    Scalar s2 = Quantity.of(0.0, "m");
    _checkPlusSymmetry(s1, s2);
    assertEquals(s1.add(s2).toString(), s2.toString()); // result in numeric precision
  }

  @Test
  public void testPlusUnits4() {
    Scalar s1 = Quantity.of(0, "m"); //
    Scalar s2 = Quantity.of(0.0, "");
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
  }

  @Test
  public void testPlusUnits5() {
    Scalar s1 = Quantity.of(0.0, "m"); //
    Scalar s2 = RealScalar.ZERO;
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
  }

  @Test
  public void testPlusMix() {
    Scalar s1 = Quantity.of(0, "m"); //
    Scalar s2 = Quantity.of(2, "kg");
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testPlusMix2() {
    Scalar s1 = Quantity.of(3, "m"); //
    Scalar s2 = Quantity.of(0, "kg");
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testPlusMix3() {
    Scalar s1 = Quantity.of(0, "m"); //
    Scalar s2 = Quantity.of(0, "kg");
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testPlusMixFail() {
    Scalar s1 = Quantity.of(1.0, "m"); //
    Scalar s2 = GaussScalar.of(0, 7);
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testPlusMixZeroFail() {
    Scalar s1 = Quantity.of(0.0, "m"); //
    Scalar s2 = GaussScalar.of(0, 7);
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testComplex() {
    Scalar s1 = ComplexScalar.of(1, 2);
    Scalar s2 = Quantity.of(0, "m*s");
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testAddDifferent() {
    Scalar s1 = Quantity.of(200, "g"); //
    Scalar s2 = Quantity.of(1, "kg");
    Scalar sum = Total.ofVector(Tensors.of(s1, s2).map(UnitSystem.SI()));
    assertEquals(sum, Scalars.fromString("6/5[kg]"));
  }

  @Test
  public void testPlusFail() {
    assertThrows(TensorRuntimeException.class, () -> Quantity.of(2, "m").add(Quantity.of(2, "kg")));
    assertThrows(Exception.class, () -> _checkPlusSymmetry( //
        Quantity.of(ComplexScalar.of(1, 2), "m"), //
        Quantity.of(2, "kg")));
  }
}
