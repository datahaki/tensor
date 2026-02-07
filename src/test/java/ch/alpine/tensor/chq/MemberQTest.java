// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.num.Pi;

class MemberQTest {
  static class CheckMemberQ implements MemberQ {
    @Override
    public boolean isMember(Tensor tensor) {
      return tensor.length() == 3;
    }
  }

  @Test
  void testRequire() {
    CheckMemberQ checkMemberQ = new CheckMemberQ();
    Tensor v = UnitVector.of(3, 1);
    assertSame(v, checkMemberQ.requireMember(v));
    assertThrows(Exception.class, () -> checkMemberQ.requireMember(Pi.VALUE));
  }
}
