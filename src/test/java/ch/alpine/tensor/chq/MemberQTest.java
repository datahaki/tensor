// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.num.Pi;

class MemberQTest {
  static class CheckMemberQ implements MemberQ {
    @Override
    public boolean test(Tensor tensor) {
      return tensor.length() == 3;
    }
  }

  @Test
  void testRequire() {
    CheckMemberQ checkMemberQ = new CheckMemberQ();
    Tensor v = UnitVector.of(3, 1);
    assertSame(v, checkMemberQ.require(v));
    assertThrows(Exception.class, () -> checkMemberQ.require(Pi.VALUE));
  }

  @Test
  void testAll() {
    MemberQ m0 = t -> t.Get(0).equals(RealScalar.ONE);
    MemberQ m1 = t -> t.Get(1).equals(RealScalar.ONE);
    Tensor v = Tensors.vector(1, 0);
    assertTrue(MemberQ.any(m0, m1).test(v));
    assertFalse(MemberQ.all(m0, m1).test(v));
  }
}
