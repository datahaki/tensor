// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class EditDistanceTest {
  private static void symm(int exp, String s1, String s2) {
    assertEquals(exp, EditDistance.of(s1, s2));
    assertEquals(exp, EditDistance.of(s2, s1));
    assertEquals(exp, EditDistance.function(s1).apply(s2));
    assertEquals(exp, EditDistance.function(s2).apply(s1));
    assertEquals(exp, EditDistanceTraced.of(s1, s2));
    assertEquals(exp, EditDistanceTraced.of(s2, s1));
  }

  @Test
  void test() {
    symm(2, "dolle", "dele");
    symm(4, "abcd", "");
    symm(0, "abcd", "abcd");
    symm(8, "aaaaaaabbbbaaab", "aabbaaa");
    symm(1, "jan", "jana");
    symm(1, "jan", "ajan");
    symm(2, "jna", "jan");
    symm(1, "flotti", "lotti");
    symm(2, "flotti", "lottif");
    symm(1, "lotti", "losti");
    symm(1, "a", "b");
    symm(1, "a", "");
    symm(3, "lotti", "karotti");
    symm(7, "gatacagataca", "acggcctata");
  }

  @RepeatedTest(10)
  void testRandom() {
    String s1 = RandomVariate.of(UniformDistribution.unit(), 6).toString();
    String s2 = RandomVariate.of(UniformDistribution.unit(), 4).toString();
    assertEquals(EditDistance.of(s1, s2), EditDistanceTraced.of(s1, s2));
    assertEquals(EditDistance.of(s2, s1), EditDistanceTraced.of(s2, s1));
  }

  @Disabled
  @Test
  void testLarge() {
    String s1 = RandomVariate.of(UniformDistribution.unit(), 500).toString();
    String s2 = RandomVariate.of(UniformDistribution.unit(), 500).toString();
    System.out.println(s1.length());
    System.out.println(s2.length());
    EditDistance.of(s1, s2);
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> EditDistance.function(null));
  }
}
