// code by jph
package ch.alpine.tensor;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TensorParserTest extends TestCase {
  public void testScalar() {
    assertEquals(Tensors.fromString("123"), RealScalar.of(123));
    assertEquals(Tensors.fromString("5/4[A]"), Quantity.of(RationalScalar.of(5, 4), "A"));
    assertEquals(Tensors.fromString("-3"), RealScalar.of(-3));
    assertEquals(Tensors.fromString("3/19"), RationalScalar.of(3, 19));
  }

  public void testVector() {
    assertEquals(Tensors.fromString(" {123}  "), Tensors.vector(123));
    assertEquals(Tensors.fromString(" { 1  , 2    ,3   ,4}  "), Tensors.vector(1, 2, 3, 4));
  }

  public void testNested() {
    assertEquals(Tensors.fromString(" {123}  "), Tensors.vector(123));
    assertEquals(Tensors.fromString(" { 1  , {2,2/3}    ,3 ,{},{   }  ,4}  "), Tensors.of( //
        RealScalar.of(1), //
        Tensors.of(RealScalar.TWO, RationalScalar.of(2, 3)), //
        RealScalar.of(3), //
        Tensors.empty(), //
        Tensors.empty(), //
        RealScalar.of(4)));
  }

  public void testVectorString() {
    assertEquals(Tensors.fromString(" {, 1  , 2  ,    ,4  ,,6  ,7,}  ").length(), 9);
    assertEquals(Tensors.fromString(" { 1  , 2  ,  ,3  ,,1  ,4}  ").length(), 7);
    assertEquals(Tensors.fromString("{2.2,3,}").length(), 3);
  }

  public void testStrangeBrackets() {
    _checkString("}");
    _checkString("{");
    _checkString(",");
    _checkString("{ } { }");
    _checkString(" { } { } ");
    _checkString("{{}");
    _checkString(" } { ");
    _checkString(" } { } { ");
    _checkString(" { {} { } }");
    _checkString(" { {}, { } {} }");
    _checkString(" { {} { } ,{} }");
    _checkString("  {} { } }");
  }

  public void testChars() {
    Tensor tensor = Tensors.fromString("{{a, b, a}, {a, a, b}, {b, a, a}}");
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3));
  }

  public void testFromString() {
    assertEquals(Tensors.fromString("{   }"), Tensors.empty());
    assertEquals(Tensors.fromString("{ 2 ,-3   , 4}"), Tensors.vector(2, -3, 4));
    assertEquals(Tensors.fromString("{   {2, -3, 4  }, {2.3,-.2   }, {  }   }"), //
        Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(2.3, -.2), Tensors.empty()));
  }

  public void testFromString2() {
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
    _checkString("{2, {1, 2}a}");
    _checkString("{{0}1}");
    _checkString("{{1, 1}1}");
    _checkString("{{1, 2}a}");
    _checkString("{{1, 2}a, 1}");
    _checkString("{2, {1, 2}123}");
  }

  public void testEmptyString() {
    assertEquals(Tensors.fromString(""), StringScalar.of(""));
    assertEquals(Tensors.fromString("   "), StringScalar.of("   "));
    assertEquals(Tensors.fromString(" \t  "), StringScalar.of(" \t  "));
  }

  public void testFromStringFunction() {
    assertEquals(Tensors.fromString("{ 2 ,-3   , 4}", string -> RealScalar.of(3)), Tensors.vector(3, 3, 3));
  }

  public void testFromStringFunctionNested() {
    assertEquals(Tensors.fromString("{ 2 ,{-3   , 4} }", string -> RealScalar.of(3)), Tensors.fromString("{3, {3, 3}}"));
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

  private static final Random RANDOM = new Random();

  private static Tensor generate(int level, int max) {
    if (level < max && 0.2 < RANDOM.nextDouble())
      return Tensor.of(IntStream.range(0, RANDOM.nextInt(4)).mapToObj(i -> generate(level + 1, max)));
    return RealScalar.of(RANDOM.nextInt(100));
  }

  public void testRandom() {
    for (int count = 0; count < 20; ++count) {
      Tensor tensor = generate(0, 4);
      assertEquals(tensor, Tensors.fromString(tensor.toString()));
    }
  }

  public void testFailStringNull() {
    AssertFail.of(() -> Tensors.fromString(null));
  }

  public void testVisibility() {
    assertEquals(TensorParser.class.getModifiers() & 1, 0);
  }
}
