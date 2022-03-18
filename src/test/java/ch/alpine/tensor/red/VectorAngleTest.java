// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;

public class VectorAngleTest {
  @Test
  public void testReal1() {
    Tensor id = IdentityMatrix.of(5);
    Scalar s1 = VectorAngle.of(id.get(2).multiply(RealScalar.of(3)), id.get(4)).get();
    assertEquals(s1.toString(), "1.5707963267948966");
    Scalar s2 = VectorAngle.of(id.get(2), id.get(2)).get();
    assertEquals(s2, RealScalar.ZERO);
  }

  @Test
  public void testReal2() {
    Tensor u = Tensors.vector(1, 2, 3);
    Tensor v = Tensors.vector(-6, -3, 2);
    Scalar s1 = VectorAngle.of(u, v).get();
    assertEquals(s1.toString(), "1.8019298645941293"); // mathematica
  }

  @Test
  public void testComplex() {
    Tensor u = Tensors.fromString("{0+1*I, 3/4-1*I}");
    Tensor v = Tensors.fromString("{1+1*I, -1/2+2*I}");
    Scalar s1 = VectorAngle.of(u, v).get();
    assertEquals(s1.toString(), "1.921525068221019"); // mathematica
  }

  @Test
  public void testComplex2() {
    Tensor u = Tensors.fromString("{0.6246950475544243*I, 0.4685212856658182-0.6246950475544243*I}");
    Tensor v = Tensors.fromString("{0.4+0.4*I, -0.2+0.8*I}");
    // mathematica gives 1.9215250682210188` - 2.8189256484623115`*^-17 I +
    Scalar s1 = VectorAngle.of(u, v).get();
    assertTrue(s1 instanceof RealScalar);
    Chop._14.requireClose(s1, Scalars.fromString("1.921525068221019"));
  }

  @Test
  public void testLarge() {
    Tensor u = Tensors.vector(1e300, 0);
    Tensor v = Tensors.vector(1e300, 1e300);
    Chop._14.requireClose(VectorAngle.of(u, v).get(), DoubleScalar.of(0.7853981633974484));
  }

  @Test
  public void testSmall() {
    Tensor u = Tensors.vector(1e-300, 0);
    Tensor v = Tensors.vector(-1e-300, -1e-300);
    Chop._14.requireClose(VectorAngle.of(u, v).get(), DoubleScalar.of(2.356194490192345));
  }

  @Test
  public void testFail() {
    assertThrows(ClassCastException.class, () -> VectorAngle.of(HilbertMatrix.of(3), HilbertMatrix.of(3)));
  }

  @Test
  public void testSingle() {
    assertEquals(VectorAngle.of(Tensors.vector(1), Tensors.vector(2)).get(), RealScalar.ZERO);
    assertEquals(VectorAngle.of(Tensors.vector(1), Tensors.vector(-2)).get(), RealScalar.of(Math.PI));
  }

  @Test
  public void testZero() {
    assertFalse(VectorAngle.of(Tensors.vector(0, 0), Tensors.vector(1, 0)).isPresent());
    assertFalse(VectorAngle.of(Tensors.vector(0.0, 0.0), Tensors.vector(1.0, 0.0)).isPresent());
    assertFalse(VectorAngle.of(Tensors.vector(1.0, 1.0), Tensors.vector(0.0, 0.0)).isPresent());
    assertFalse(VectorAngle.of(Tensors.vector(0, 0), Tensors.vector(0, 0)).isPresent());
  }

  @Test
  public void testLengthFail() {
    assertThrows(IllegalArgumentException.class, () -> VectorAngle.of(Tensors.vector(1, 0, 0), Tensors.vector(1, 0)));
    assertThrows(IllegalArgumentException.class, () -> VectorAngle.of(Tensors.vector(0, 0, 0), Tensors.vector(1, 0)));
    assertThrows(IllegalArgumentException.class, () -> VectorAngle.of(Tensors.vector(1, 0, 0), Tensors.vector(0, 0)));
  }
}
