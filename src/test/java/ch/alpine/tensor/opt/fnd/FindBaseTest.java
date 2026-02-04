package ch.alpine.tensor.opt.fnd;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import test.SerializableQ;

class FindBaseTest {
  @Test
  void test() {
    SerializableQ.require(FindMaximum.of(Sin.FUNCTION));
    SerializableQ.require(FindMinimum.of(Cos.FUNCTION));
  }
}
