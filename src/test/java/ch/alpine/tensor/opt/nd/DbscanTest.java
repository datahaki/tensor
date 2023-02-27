// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;

class DbscanTest {
  @Test
  void testSimple() {
    Distribution dist_b = UniformDistribution.of(0, 10);
    Distribution dist_r = NormalDistribution.of(0, 1);
    Tensor points = Tensors.empty();
    Tensor base = RandomVariate.of(dist_b, 5, 2);
    for (int index = 0; index < 10; ++index)
      for (Tensor r : base)
        for (Tensor p : RandomVariate.of(dist_r, 10, 2))
          points.append(r.add(p));
    // Timing timing = Timing.started();
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_2_NORM, RealScalar.of(0.5), 3);
    // double seconds = timing.seconds();
    // assertTrue(seconds < 2);
    assertEquals(integers.length, points.length());
    Map<Integer, Tensor> map = new HashMap<>();
    IntStream.range(0, integers.length) //
        .forEach(index -> map.computeIfAbsent(integers[index], i -> Tensors.empty()).append(points.get(index)));
    // System.out.println(map.size());
  }

  @Test
  void testUniform() {
    Tensor points = Range.of(0, 8).map(Tensors::of);
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_1_NORM, RealScalar.of(1), 3);
    assertEquals(Tensors.vector(integers), Array.zeros(integers.length));
  }

  @Test
  void testInsufficientRadius() {
    Tensor points = Range.of(0, 8).map(Tensors::of);
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_2_NORM, RealScalar.of(0.1), 2);
    assertEquals(Tensors.vector(integers), ConstantArray.of(RealScalar.of(-1), integers.length));
  }

  @Test
  void testInsufficientPoints() {
    Tensor points = Range.of(0, 8).map(Tensors::of);
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_INFINITY_NORM, RealScalar.of(1), 4);
    assertEquals(Tensors.vector(integers), ConstantArray.of(RealScalar.of(-1), integers.length));
  }

  @Test
  void testQuantity() {
    Tensor points = Range.of(0, 8).map(Tensors::of).map(s -> Quantity.of(s, "m"));
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_2_NORM, Quantity.of(1, "m"), 3);
    assertEquals(Tensors.vector(integers), Array.zeros(integers.length));
  }

  @Test
  void testSpaced() {
    Tensor points = Join.of(Range.of(0, 7), Range.of(10, 15), Range.of(20, 24)).map(Tensors::of);
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_INFINITY_NORM, Quantity.of(1, ""), 3);
    assertEquals(Tensors.vector(integers), Tensors.vector(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2));
  }

  @Test
  void testSpacedPermuted() {
    Tensor points = Join.of(Range.of(0, 7), Range.of(10, 15), Range.of(20, 24)).map(Tensors::of);
    List<Integer> perm = IntStream.range(0, points.length()).boxed().collect(Collectors.toList());
    Collections.shuffle(perm);
    points = Tensor.of(perm.stream().map(points::get));
    Integer[] integers = Dbscan.of(points, NdCenters.VECTOR_1_NORM, Quantity.of(1, ""), 3);
    Map<Tensor, Long> map = Tally.of(Tensors.vector(integers)); // for instance {1=7, 2=5, 0=4}
    assertEquals(map.size(), 3);
    assertTrue(map.values().contains(4L));
    assertTrue(map.values().contains(5L));
    assertTrue(map.values().contains(7L));
  }
  // @Test
  // void testReIm() {
  // Dbscan.of(null, NdCenters.VECTOR_2_NORM, RealScalar.of(1e-12), 1);
  // }

  @Test
  void testFail() {
    Tensor points = Range.of(0, 8).map(Tensors::of);
    assertThrows(Throw.class, () -> Dbscan.of(points, NdCenters.VECTOR_2_NORM, RealScalar.of(-1.1), 3));
    assertThrows(IllegalArgumentException.class, () -> Dbscan.of(points, NdCenters.VECTOR_2_NORM, RealScalar.of(+1.1), 0));
  }
}
