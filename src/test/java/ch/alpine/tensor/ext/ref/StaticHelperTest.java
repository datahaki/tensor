// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.re.LinearSolve;

class StaticHelperTest {
  @Test
  void test() {
    assertTrue(StaticHelper.isInSubpackageOf(LinearSolve.class, "ch.alpine.tensor.mat"));
    assertTrue(StaticHelper.isInSubpackageOf(LinearSolve.class, "ch.alpine.tensor.mat.re"));
    assertFalse(StaticHelper.isInSubpackageOf(LinearSolve.class, "ch.alpine.tensor.mat.r"));
  }
}
