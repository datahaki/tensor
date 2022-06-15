// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class MathematicaFormatTest {
  @Test
  public void testMathematica() {
    int n = 20;
    int m = 10;
    Random random = new Random();
    Tensor a = Tensors.matrix((i, j) -> //
    random.nextInt(3) == 0 ? //
        DoubleScalar.of(random.nextDouble()) : //
        RationalScalar.of(random.nextLong(), random.nextLong()), n, m);
    assertEquals(MathematicaFormat.of(a).count(), n); // count rows
    assertEquals(a, MathematicaFormat.parse(MathematicaFormat.of(a))); // full circle
  }

  private static void checkNonString(Tensor tensor) {
    Optional<Scalar> optional = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .filter(scalar -> scalar instanceof StringScalar) //
        .findAny();
    boolean containsStringScalar = optional.isPresent();
    if (containsStringScalar)
      System.out.println(optional.get());
    assertFalse(containsStringScalar);
    assertFalse(StringScalarQ.any(tensor));
  }

  @Test
  public void testStrings() {
    String[] strings = new String[] { //
        "{{3 + 2*I}, I,-I,-1.0348772853950305 + 0.042973906265653894*I, ", //
        " -1.0348772853950305 - 0.042973906265653894*I, {}, ", //
        " {3.141592653589793, {3, 1.4142135623730951}, 23846238476583465873465/", //
        "   234625348762534876523847652837645223864521}}" };
    Tensor tensor = MathematicaFormat.parse(Stream.of(strings));
    checkNonString(tensor);
  }

  @Test
  public void testComplex() {
    String[] strings = new String[] { //
        "{{3 + I}, -1.0348772853950305 - 0.042973906265653894*I, {}, ", //
        " 0.1 + I, 0.1 - I, ", // <- these were manually added
        " 0. + 0.123*I, 0. - 123233.323123*I, {0. + 1982.6716245387552*I,", //
        "  {(81263581726538*I)/42921390881, 0. + 892.5158065769785*I}} }" };
    Tensor tensor = MathematicaFormat.parse(Stream.of(strings));
    checkNonString(tensor);
    assertEquals(tensor.length(), 8);
  }

  @Test
  public void testBasic() throws IOException {
    String string = getClass().getResource("/io/basic.mathematica").getPath();
    if (System.getProperty("os.name").contains("Windows") && string.charAt(0) == '/') {
      string = string.substring(1);
    }
    Tensor tensor = Get.of(Paths.get(string).toFile());
    checkNonString(tensor);
  }

  @Test
  public void testBasicResource() {
    Tensor tensor = ResourceData.of("/io/basic.mathematica");
    checkNonString(tensor);
  }

  @Test
  public void testExponent() throws IOException {
    String string = getClass().getResource("/io/exponent.mathematica").getPath();
    if (System.getProperty("os.name").contains("Windows") && string.charAt(0) == '/') {
      string = string.substring(1);
    }
    Tensor tensor = Get.of(Paths.get(string).toFile());
    checkNonString(tensor);
    assertEquals(tensor, ResourceData.of("/io/exponent.mathematica"));
  }

  @Test
  public void testExponent2() {
    Tensor tensor = MathematicaFormat.parse(Stream.of("{1*^-10, 1*^10}"));
    checkNonString(tensor);
    String put = Put.string(tensor);
    Tensor recon = MathematicaFormat.parse(Stream.of(put));
    assertEquals(tensor, recon);
  }

  @Test
  public void testPrime() throws IOException {
    String string = getClass().getResource("/io/prime.mathematica").getPath();
    if (System.getProperty("os.name").contains("Windows") && string.charAt(0) == '/') {
      string = string.substring(1);
    }
    // System.out.println(Paths.get(string).toFile());
    Tensor tensor = Get.of(Paths.get(string).toFile());
    assertTrue(tensor.stream().anyMatch(scalar -> scalar instanceof DecimalScalar));
    checkNonString(tensor);
    assertEquals(tensor.toString(), Put.string(tensor));
  }
}
