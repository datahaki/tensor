// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;

class MathematicaFormatTest {
  @Test
  void testMathematica() {
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
      fail("" + optional.get());
    assertFalse(containsStringScalar);
    assertFalse(StringScalarQ.any(tensor));
  }

  @Test
  void testStrings() {
    String[] strings = new String[] { //
        "{{3 + 2*I}, I,-I,-1.0348772853950305 + 0.042973906265653894*I, ", //
        " -1.0348772853950305 - 0.042973906265653894*I, {}, ", //
        " {3.141592653589793, {3, 1.4142135623730951}, 23846238476583465873465/", //
        "   234625348762534876523847652837645223864521}}" };
    Tensor tensor = MathematicaFormat.parse(Stream.of(strings));
    checkNonString(tensor);
  }

  @Test
  void testComplex() {
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
  void testBasic() throws IOException {
    File file = OperatingSystem.fileOfResource("/ch/alpine/tensor/io/basic.mathematica");
    Tensor tensor = Get.of(file);
    checkNonString(tensor);
  }

  @Test
  void testBasicResource() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/io/basic.mathematica");
    checkNonString(tensor);
  }

  @Test
  void testExponent() throws IOException {
    File file = OperatingSystem.fileOfResource("/ch/alpine/tensor/io/exponent.mathematica");
    Tensor tensor = Get.of(file);
    checkNonString(tensor);
    assertEquals(tensor, ResourceData.of("/ch/alpine/tensor/io/exponent.mathematica"));
  }

  @Test
  void testExponent2() {
    Tensor tensor = MathematicaFormat.parse(Stream.of("{1*^-10, 1*^10}"));
    checkNonString(tensor);
    String put = Put.string(tensor);
    Tensor recon = MathematicaFormat.parse(Stream.of(put));
    assertEquals(tensor, recon);
  }

  @Test
  void testPrime() throws IOException {
    File file = OperatingSystem.fileOfResource("/ch/alpine/tensor/io/decimals.mathematica");
    Tensor tensor = Get.of(file);
    assertTrue(tensor.stream().anyMatch(scalar -> scalar instanceof DecimalScalar));
    checkNonString(tensor);
    assertEquals(tensor.toString(), Put.string(tensor));
  }

  @Test
  void testSparse() {
    Tensor sparse = LeviCivitaTensor.of(3);
    List<String> list = MathematicaFormat.of(sparse).collect(Collectors.toList());
    assertEquals(list.get(0), "SparseArray[{{1, 2, 3}->1,");
  }

  @Test
  void testSparseNested() {
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2, {3, 8.45`}}}");
    tensor.set(IdentityMatrix.sparse(3), 0, 2);
    List<String> list = MathematicaFormat.of(tensor).collect(Collectors.toList());
    assertFalse(list.isEmpty());
    // list.forEach(System.out::println);
  }

  @Test
  void testOf() {
    String string = MathematicaFormat.concise("Function", 12);
    assertEquals(string, "Function[12]");
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> MathematicaFormat.of(null));
    assertThrows(Exception.class, () -> MathematicaFormat.concise(null, 12));
  }

  @Test
  void testSmallMatrix() {
    String string = new Throw(RealScalar.of(3), 12).getMessage();
    assertEquals(string, "Throw[3, 12]");
  }
}
