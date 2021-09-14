// code by jph
package ch.alpine.tensor.alg;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.RandomPermutation;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testIdentity() {
    int[] permute = StaticHelper.inverse(new int[] { 2, 3, 4 }, new int[] { 0, 1, 2 });
    assertEquals(permute[0], 2);
    assertEquals(permute[1], 3);
    assertEquals(permute[2], 4);
  }

  public void testInv() {
    int[] src = new int[] { 2, 0, 3, 4, 1 };
    int[] dst = StaticHelper.inverse(src);
    int[] rem = StaticHelper.inverse(dst);
    assertEquals(Tensors.vectorInt(src), Tensors.vectorInt(rem));
  }

  public void testInv2() {
    int[] src = new int[] { 2, 0, 3, 4, 1 };
    int[] sigma = StaticHelper.inverse(src);
    int[] size = new int[] { 4, 1, 0, 3, 2 };
    int[] result = StaticHelper.inverse(size, sigma);
    // System.out.println(Tensors.vectorInt(result));
    int[] value = new int[src.length];
    for (int index = 0; index < src.length; ++index)
      value[index] = size[src[index]];
    // System.out.println(Tensors.vectorInt(value));
    assertEquals(Tensors.vectorInt(result), Tensors.vectorInt(value));
  }

  public void testMultiCheck() {
    for (int count = 0; count < 50; ++count) {
      int n = 4 + (count % 5);
      int[] src = RandomPermutation.ofLength(n);
      int[] sigma = StaticHelper.inverse(src);
      int[] size = RandomPermutation.ofLength(n);
      int[] result = StaticHelper.inverse(size, sigma);
      int[] value = StaticHelper.reorder(size, src);
      assertEquals(Tensors.vectorInt(result), Tensors.vectorInt(value));
    }
  }

  public void testRotate() {
    int[] permute = StaticHelper.inverse(new int[] { 2, 3, 4 }, new int[] { 2, 0, 1 });
    assertEquals(permute[0], 3);
    assertEquals(permute[1], 4);
    assertEquals(permute[2], 2);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }

  public void testReorder() {
    List<Integer> list = StaticHelper.reorder(Arrays.asList(2, 3, 4), new int[] { 2, 0, 1 });
    assertEquals(list, Arrays.asList(4, 2, 3));
  }

  public void testPermuteList() {
    List<Integer> list = StaticHelper.inverse(Arrays.asList(2, 3, 4), Tensors.vector(2, 0, 1));
    assertEquals(list, Arrays.asList(3, 4, 2));
  }
}
