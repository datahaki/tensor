// code by jph
package ch.alpine.tensor.opt.fnd;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import test.wrap.SerializableQ;

class FindBaseTest {
  @Test
  void test() {
    SerializableQ.require(FindMaximum.of(Sin.FUNCTION));
    SerializableQ.require(FindMinimum.of(Cos.FUNCTION));
  }

  @Test
  void testVis() {
    assertFalse(Modifier.isPublic(FindBase.class.getModifiers()));
  }
}
