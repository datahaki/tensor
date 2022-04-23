// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Power;

public class BinomialTest {
  @Test
  public void testBasic() {
    // assertEquals(Binomial.of(10, Integer.MIN_VALUE), RealScalar.ZERO);
    assertEquals(Binomial.of(10, -13), RealScalar.ZERO);
    assertEquals(Binomial.of(10, -1), RealScalar.ZERO);
    assertEquals(Binomial.of(10, 0), RealScalar.ONE);
    assertEquals(Binomial.of(10, 3), RealScalar.of(120));
    assertEquals(Binomial.of(10, 10), RealScalar.ONE);
    assertEquals(Binomial.of(10, 11), RealScalar.ZERO);
    assertEquals(Binomial.of(10, Integer.MAX_VALUE), RealScalar.ZERO);
  }

  @Test
  public void testSingleIn() {
    assertEquals(Binomial.of(10).over(0), RealScalar.ONE);
    assertEquals(Binomial.of(RealScalar.of(10)).over(3), RealScalar.of(120));
    assertEquals(Binomial.of(10).over(10), RealScalar.ONE);
  }

  @Test
  public void testTreadSafe() {
    IntStream.range(0, 2000).parallel() //
        .forEach(n -> Binomial.of(10 + (n % 500)));
  }

  @Test
  public void testOrder() {
    assertEquals(Binomial.of(+0, 0), RealScalar.ONE);
    assertEquals(Binomial.of(+0, 7), RealScalar.ZERO);
    assertEquals(Binomial.of(+3, 7), RealScalar.ZERO);
    assertEquals(Binomial.of(-3, 7), RealScalar.of(-36));
  }

  @Test
  public void testDecimal() {
    Chop._08.requireClose(Binomial.of(RealScalar.of(5), RealScalar.of(8.915)), DoubleScalar.of(-0.0001814896744175351));
    Chop._08.requireClose(Binomial.of(RealScalar.of(3.21), RealScalar.of(4.5)), DoubleScalar.of(-0.03395179589776722));
    Chop._08.requireClose(Binomial.of(RealScalar.of(10.21), RealScalar.of(3)), DoubleScalar.of(128.66999350000037));
    Chop._08.requireClose(Binomial.of(RealScalar.of(8.81), RealScalar.of(11.3)), DoubleScalar.of(0.0011937860196171754));
  }

  @Test
  public void testScalar() {
    assertEquals(Binomial.of(RealScalar.of(4), RealScalar.of(2)), RealScalar.of(6));
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Binomial binomial = Serialization.copy(Binomial.of(3));
    assertEquals(binomial.over(2), RealScalar.of(3));
  }

  @Test
  public void testGeneralNK() {
    for (int n = 0; n < 5; ++n)
      for (int k = -5; k <= 5; ++k) {
        Scalar bpos = Binomial.of(n, k);
        Scalar bneg = Power.of(-1, n + k).multiply(Binomial.of(-k - 1, -n - 1));
        assertEquals(bpos, bneg);
      }
  }

  @Test
  public void testKZero() {
    for (int n = -5; n <= 5; ++n)
      assertEquals(Binomial.of(n, 0), RealScalar.ONE);
  }

  @Test
  public void testKNeg1() {
    for (int n = -5; n <= 5; ++n)
      assertEquals(Binomial.of(n, -1), Boole.of(n == -1));
  }

  @Test
  public void testKNeg2() {
    for (int n = 0; n <= 5; ++n)
      assertEquals(Binomial.of(n, -2), RealScalar.ZERO);
  }

  @Test
  public void testSpecial() {
    assertEquals(Binomial.of(-4, 0), RealScalar.ONE);
    assertEquals(Binomial.of(-4, -1), RealScalar.ZERO);
    assertEquals(Binomial.of(-4, -2), RealScalar.ZERO);
    assertEquals(Binomial.of(-4, -3), RealScalar.ZERO);
    assertEquals(Binomial.of(-4, -4), RealScalar.ONE);
    assertEquals(Binomial.of(-4, -5), RealScalar.of(-4));
    assertEquals(Binomial.of(-4, -10), RealScalar.of(84));
  }

  @Test
  public void testFailN() {
    assertThrows(TensorRuntimeException.class, () -> Binomial.of(RealScalar.of(10.21)));
    assertThrows(IllegalArgumentException.class, () -> Binomial.of(-1));
  }

  @Test
  public void testKOutside() {
    assertEquals(Binomial.of(10, -10), RealScalar.ZERO);
    assertEquals(Binomial.of(10, -1), RealScalar.ZERO);
    assertEquals(Binomial.of(10, 11), RealScalar.ZERO);
    assertEquals(Binomial.of(10, 20), RealScalar.ZERO);
  }

  @Test
  public void testLarge() {
    Scalar res = Binomial.of(1000, 500);
    BigInteger bi = new BigInteger(
        "270288240945436569515614693625975275496152008446548287007392875106625428705522193898612483924502370165362606085021546104802209750050679917549894219699518475423665484263751733356162464079737887344364574161119497604571044985756287880514600994219426752366915856603136862602484428109296905863799821216320");
    assertEquals(res, RealScalar.of(bi));
  }

  @Test
  public void testPascal() {
    assertEquals(Tensors.vector(Binomial.of(0)::over, 1), Tensors.vector(1));
    assertEquals(Tensors.vector(Binomial.of(1)::over, 2), Tensors.vector(1, 1));
    assertEquals(Tensors.vector(Binomial.of(2)::over, 3), Tensors.vector(1, 2, 1));
    assertEquals(Tensors.vector(Binomial.of(3)::over, 4), Tensors.vector(1, 3, 3, 1));
  }

  @Test
  public void testHuge() {
    assertEquals(Binomial.of(1000000, 2), RealScalar.of(499999500000L));
    assertEquals(Binomial.of(1000000, 1000000 - 2), RealScalar.of(499999500000L));
  }

  @Test
  public void testLargeFail() {
    assertThrows(TensorRuntimeException.class, () -> Binomial.of(RealScalar.of(-123412341234324L), RealScalar.ZERO));
  }

  @Test
  public void testNegOne() {
    for (int n = -10; n < 10; ++n)
      assertEquals(Binomial.of(n, -1), Boole.of(n == -1));
  }

  @Test
  public void testAltSym() {
    assertEquals(Tensors.vector(k -> Binomial.of(k - 5, 1), 11), Tensors.vector(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5));
    assertEquals(Tensors.vector(k -> Binomial.of(k - 5, 2), 11), Tensors.vector(15, 10, 6, 3, 1, 0, 0, 1, 3, 6, 10));
    assertEquals(Tensors.vector(k -> Binomial.of(k - 5, 3), 11), Tensors.vector(-35, -20, -10, -4, -1, 0, 0, 0, 1, 4, 10));
  }

  @Test
  public void testVectors() {
    assertEquals(Tensors.vector(k -> Binomial.of(3, k - 5), 11), Tensors.vector(0, 0, 0, 0, 0, 1, 3, 3, 1, 0, 0));
    assertEquals(Tensors.vector(k -> Binomial.of(k - 5, -3), 11), Tensors.vector(0, 0, 1, -2, 1, 0, 0, 0, 0, 0, 0));
    assertEquals(Tensors.vector(k -> Binomial.of(k - 5, -4), 11), Tensors.vector(0, 1, -3, 3, -1, 0, 0, 0, 0, 0, 0));
  }

  @Test
  public void testBinomialTable() {
    Tensor tableb = Tensors.matrix((i, j) -> Binomial.of(i - 5, j - 5), 11, 11);
    Tensor tensor = ResourceData.of("/alg/binomial11.csv");
    assertEquals(tableb, tensor);
  }

  @Test
  public void testToString() {
    Binomial binomial = Binomial.of(13);
    assertEquals(binomial.toString(), "Binomial[13]");
  }

  @Test
  public void testGaussScalar() {
    int prime = 251;
    Scalar n = GaussScalar.of(4, prime);
    Tensor row = Tensors.of(n.one());
    Scalar top = n;
    for (Scalar j = GaussScalar.of(1, prime); true; j = j.add(n.one())) {
      Scalar x = Last.of(row);
      row.append(x.multiply(top).divide(j));
      if (j.equals(n))
        break;
      top = top.subtract(n.one());
    }
    // System.out.println(row);
  }

  @Test
  public void testGaussScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Binomial.of(GaussScalar.of(3, 5), GaussScalar.of(2, 5)));
  }
}
