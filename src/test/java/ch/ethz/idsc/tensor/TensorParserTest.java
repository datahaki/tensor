// code by jph
package ch.ethz.idsc.tensor;

import java.util.regex.Pattern;

import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import junit.framework.TestCase;

public class TensorParserTest extends TestCase {
  public void testFromString() {
    assertEquals(Tensors.fromString("{   }"), Tensors.empty());
    assertEquals(Tensors.fromString("{ 2 ,-3   , 4}"), Tensors.vector(2, -3, 4));
    assertEquals(Tensors.fromString("  {   {2, -3, 4  }, {2.3,-.2   }, { \t }  }  \t "), //
        Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(2.3, -.2), Tensors.empty()));
  }

  private static void _checkString(String string) {
    assertEquals(Tensors.fromString(string), StringScalar.of(string));
  }

  public void testFailBug() {
    _checkString("{2.5");
    _checkString("{2.5,");
    _checkString("{2.5,}}");
    _checkString("{2.5,},}");
    _checkString("{2.5}}");
  }

  public void testComma() {
    Tensor scalar = Tensors.fromString("3.12,");
    assertTrue(scalar instanceof StringScalar);
  }

  public void testEmptyPost() {
    Tensor vector = Tensors.fromString("{2.2,3,}");
    assertEquals(vector.length(), 3);
    assertTrue(vector.Get(2) instanceof StringScalar);
  }

  public void testEmptyAnte() {
    Tensor vector = Tensors.fromString("{,2.2,3}");
    assertEquals(vector.length(), 3);
    assertTrue(vector.Get(0) instanceof StringScalar);
  }

  public void testEmptyMid() {
    Tensor vector = Tensors.fromString("{2.2,,,3}");
    assertEquals(vector.length(), 4);
    assertTrue(vector.Get(1) instanceof StringScalar);
    assertTrue(vector.Get(2) instanceof StringScalar);
  }

  public void testExcessChars() {
    _checkString("{1, 2}a");
    _checkString("a{1, 2}");
    Tensor tensor = Tensors.fromString("{2, {1, 2}a}");
    assertEquals(tensor.Get(0), RealScalar.of(2));
    assertTrue(StringScalarQ.of(tensor.Get(1)));
  }

  public void testStrangeBrackets() {
    _checkString("{ } { }");
    _checkString("{{}");
    _checkString(" } { ");
    _checkString(" } { } { ");
  }

  public void testEmptyString() {
    assertEquals(Tensors.fromString(""), StringScalar.of(""));
    assertEquals(Tensors.fromString("   "), StringScalar.of("   "));
    assertEquals(Tensors.fromString(" \t  "), StringScalar.of(" \t  "));
  }

  public void testFromStringFunction() {
    Tensor tensor = Tensors.fromString("{ 2 ,-3   , 4}", string -> RealScalar.of(3));
    assertEquals(tensor, Tensors.vector(3, 3, 3));
  }

  public void testFromStringFunctionNested() {
    Tensor tensor = Tensors.fromString("{ 2 ,{-3   , 4} }", string -> RealScalar.of(3));
    assertEquals(tensor, Tensors.fromString("{3, {3, 3}}"));
  }

  public void testEMatlab() {
    Tensor tensor = Tensors.fromString("3e-2");
    assertTrue(StringScalarQ.any(tensor));
  }

  public void testWhitespace() {
    Pattern pattern = Pattern.compile("\\s*");
    assertFalse(pattern.matcher("   a").matches());
    assertFalse(pattern.matcher("   {").matches());
    assertTrue(pattern.matcher("  \t \n ").matches());
  }

  public void testFailStringNull() {
    try {
      Tensors.fromString(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailFunctionNull() {
    try {
      new TensorParser(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
