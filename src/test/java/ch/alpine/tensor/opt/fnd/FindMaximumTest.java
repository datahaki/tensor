package ch.alpine.tensor.opt.fnd;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.tri.Sin;
import test.wrap.SerializableQ;

class FindMaximumTest {
  @Test
  void test() {
    FindMaximum findMaximum = FindMaximum.of(Sin.FUNCTION);
    SerializableQ.require(findMaximum);
    Scalar sol = findMaximum.inside(Clips.interval(0, 5));
    Chop._06.requireClose(sol, Pi.HALF);
  }
}
