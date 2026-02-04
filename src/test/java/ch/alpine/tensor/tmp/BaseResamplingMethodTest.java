// code by jph
package ch.alpine.tensor.tmp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import test.SerializableQ;

class BaseResamplingMethodTest {
  @ParameterizedTest
  @EnumSource
  void testBasic(ResamplingMethods resamplingMethods) {
    SerializableQ.require(resamplingMethods.get());
  }
}
