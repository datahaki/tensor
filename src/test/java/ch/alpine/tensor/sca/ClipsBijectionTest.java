package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.DateTime;

class ClipsBijectionTest {
  @Test
  void testForward() {
    ClipsBijection clipsBijection = new ClipsBijection(Clips.interval(2, 9), Clips.interval(-4, -1));
    assertEquals(clipsBijection.forward().apply(RealScalar.of(2)), RealScalar.of(-4));
    assertEquals(clipsBijection.forward().apply(RealScalar.of(9)), RealScalar.of(-1));
  }

  @Test
  void testReverse() {
    ClipsBijection clipsBijection = new ClipsBijection(Clips.interval(2, 9), Clips.interval(-4, -1));
    assertEquals(clipsBijection.reverse().apply(RealScalar.of(-4)), RealScalar.of(2));
    assertEquals(clipsBijection.reverse().apply(RealScalar.of(-1)), RealScalar.of(9));
    assertTrue(clipsBijection.toString().startsWith("ClipsBijection["));
  }

  @Test
  void testExec() {
    int va = 0 < DateTime.now().year() ? 3 : fail();
    assertEquals(va, 3);
  }
}
