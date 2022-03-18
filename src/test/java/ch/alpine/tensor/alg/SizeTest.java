// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class SizeTest {
  @Test
  public void testTotal() {
    assertEquals(Size.of(Arrays.asList(4, 2, 3)).total(), 24);
    assertEquals(Size.of(Arrays.asList(3, 5, 7)).total(), 3 * 5 * 7);
  }

  @Test
  public void testString() {
    Size size = Size.of(Arrays.asList(4, 2, 3));
    String string = size.toString();
    assertFalse(string.isEmpty());
  }

  @Test
  public void testStream() {
    List<Integer> list = Stream.iterate(3, i -> i < 10, i -> i + 1).collect(Collectors.toList());
    assertEquals(list, Arrays.asList(3, 4, 5, 6, 7, 8, 9));
  }

  @Test
  public void testEmptyFail() {
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> Size.of(Arrays.asList()));
  }

  @Test
  public void testIdentity() {
    int[] permute = Size.of(Arrays.asList(2, 3, 4)).permute(new int[] { 0, 1, 2 });
    assertEquals(permute[0], 2);
    assertEquals(permute[1], 3);
    assertEquals(permute[2], 4);
  }

  @Test
  public void testRotate() {
    int[] permute = Size.of(Arrays.asList(2, 3, 4)).permute(new int[] { 2, 0, 1 });
    assertEquals(permute[0], 3);
    assertEquals(permute[1], 4);
    assertEquals(permute[2], 2);
  }

  @Test
  public void testVisibility() {
    assertEquals(Size.class.getModifiers(), 0);
  }
}
