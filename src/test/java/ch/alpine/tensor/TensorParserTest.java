// code by jph
package ch.alpine.tensor;

import java.util.Arrays;

import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TensorParserTest extends TestCase {
  public void testScalar() {
    assertEquals(TensorParser.of("123"), RealScalar.of(123));
    assertEquals(TensorParser.of("5/4[A]"), Quantity.of(RationalScalar.of(5, 4), "A"));
    assertEquals(TensorParser.of("-3"), RealScalar.of(-3));
    assertEquals(TensorParser.of("3/19"), RationalScalar.of(3, 19));
  }

  public void testVector() {
    assertEquals(TensorParser.of(" {123}  "), Tensors.vector(123));
    assertEquals(TensorParser.of(" { 1  , 2    ,3   ,4}  "), Tensors.vector(1, 2, 3, 4));
  }

  public void testNested() {
    assertEquals(TensorParser.of(" {123}  "), Tensors.vector(123));
    assertEquals(TensorParser.of(" { 1  , {2,2/3}    ,3 ,{},{   }  ,4}  "), Tensors.of( //
        RealScalar.of(1), //
        Tensors.of(RealScalar.TWO, RationalScalar.of(2, 3)), //
        RealScalar.of(3), //
        Tensors.empty(), //
        Tensors.empty(), //
        RealScalar.of(4)));
  }

  public void testVectorString() {
    System.out.println(Tensors.fromString(" { 1  , 2  ,  ,3  ,,1  ,4}  "));
    System.out.println(TensorParser.of(" { 1  , 2  ,  ,3  ,,1  ,4}  "));
    System.out.println(TensorParser.of("{2.2,3,}"));
    // assertEquals(, Tensors.vector(1, 2, 3, 4));
  }

  public void testSome() {
    System.out.println(TensorParser.of("{2, {1, 2}123}"));
    System.out.println(TensorParser.of("{2, {1, 2}a}"));
  }

  public void testStrange() {
    System.out.println(TensorParser.of(" { } { } "));
  }

  public void testChars() {
    Tensor tensor = TensorParser.of("{{a, b, a}, {a, a, b}, {b, a, a}}");
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3));
  }

  public void testFromString() {
    assertEquals(TensorParser.of("{   }"), Tensors.empty());
    assertEquals(TensorParser.of("{ 2 ,-3   , 4}"), Tensors.vector(2, -3, 4));
    assertEquals(TensorParser.of("{   {2, -3, 4  }, {2.3,-.2   }, {  }   }"), //
        Tensors.of(Tensors.vector(2, -3, 4), Tensors.vector(2.3, -.2), Tensors.empty()));
  }

  public void testFailBug() {
    assertTrue(TensorParser.of("{2.5") instanceof StringScalar);
    assertTrue(TensorParser.of("{2.5}}") instanceof StringScalar);
    assertTrue(TensorParser.of("{2.5,") instanceof StringScalar);
    assertTrue(TensorParser.of("{2.5,}}") instanceof StringScalar);
    assertTrue(TensorParser.of("{2.5,},}") instanceof StringScalar);
  }

  // public void testComma() {
  // Tensor scalar = StringFormat.parse("3.12,");
  // System.out.println(scalar);
  // assertTrue(scalar instanceof StringScalar);
  // }
  // public void testEmptyPost() {
  // Tensor vector = StringFormat.parse("{2.2,3,}");
  // assertEquals(vector.length(), 3);
  // assertTrue(vector.Get(2) instanceof StringScalar);
  // }
  // public void testEmptyAnte() {
  // Tensor vector = StringFormat.parse("{,2.2,3}");
  // assertEquals(vector.length(), 3);
  // assertTrue(vector.Get(0) instanceof StringScalar);
  // }
  // public void testEmptyMid() {
  // Tensor vector = StringFormat.parse("{2.2,,,3}");
  // assertEquals(vector.length(), 4);
  // assertTrue(vector.Get(1) instanceof StringScalar);
  // assertTrue(vector.Get(2) instanceof StringScalar);
  // }
  public void testFromStringFunction() {
    Tensor tensor = TensorParser.of("{ 2 ,-3   , 4}", string -> RealScalar.of(3));
    assertEquals(tensor, Tensors.vector(3, 3, 3));
  }

  public void testFromStringFunctionNested() {
    Tensor tensor = TensorParser.of("{ 2 ,{-3   , 4} }", string -> RealScalar.of(3));
    assertEquals(tensor, TensorParser.of("{3, {3, 3}}"));
  }

  public void testVisibility() {
    assertEquals(TensorParser.class.getModifiers() & 1, 0);
  }
}
