// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.usr.AssertFail;

public class MappedInterpolationTest {
  @Test
  public void testFloor() throws ClassNotFoundException, IOException {
    Interpolation interpolation = //
        Serialization.copy(MappedInterpolation.of(Tensors.vector(10, 20, 30, 40), Floor.FUNCTION));
    assertEquals(interpolation.get(Tensors.vector(2.8)), RealScalar.of(30));
    assertEquals(interpolation.get(Tensors.vector(1.1)), RealScalar.of(20));
  }

  @Test
  public void testFailNull() {
    AssertFail.of(() -> MappedInterpolation.of(null, Floor.FUNCTION));
  }

  @Test
  public void testFailFunctionNull() {
    AssertFail.of(() -> MappedInterpolation.of(Tensors.vector(3, 4, 5), null));
  }
}
