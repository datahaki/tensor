// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class WeiszfeldMethodTest extends TestCase {
  public static final SpatialMedian DEFAULT = WeiszfeldMethod.with(Tolerance.CHOP);

  public void testSimple() {
    Tensor tensor = Tensors.of( //
        Tensors.vector(-1, 0), //
        Tensors.vector(0, 0), //
        Tensors.vector(2, 0) //
    );
    Tensor sol = WeiszfeldMethod.with(Chop.NONE).uniform(tensor).get();
    assertEquals(sol, Tensors.vector(0, 0));
  }

  public void testMathematica() {
    Tensor points = Tensors.fromString("{{1, 3, 5}, {7, 1, 2}, {9, 3, 1}, {4, 5, 6}}");
    Tensor solution = Tensors.vector(5.6583732018553249826, 2.7448562522811917613, 3.2509991568890024191);
    Optional<Tensor> uniform = DEFAULT.uniform(points);
    Chop._08.requireClose(uniform.get(), solution);
  }

  public void testMathematicaWeighted() {
    Tensor points = Tensors.fromString("{{1, 3, 5}, {-4, 1, 2}, {3, 3, 1}, {4, 5, 6}}");
    Tensor weights = Tensors.vector(1, 3, 4, 5);
    Optional<Tensor> weighted = WeiszfeldMethod.with(Chop._10).weighted(points, weights.divide(Total.ofVector(weights)));
    Tensor solution = Tensors.vector(2.3866562926712105936, 3.5603713896189638861, 3.5379382804133292184);
    Chop._08.requireClose(weighted.get(), solution);
    Optional<Tensor> optional = DEFAULT.uniform(points);
    assertTrue(optional.isPresent());
  }

  public void testPoles() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.of( //
        Tensors.vector(-1, 0), //
        Tensors.vector(0, 0), //
        Tensors.vector(2, 10), //
        Tensors.vector(2, -10) //
    );
    SpatialMedian spatialMedian = Serialization.copy(WeiszfeldMethod.with(Chop._02));
    Tensor sol = spatialMedian.uniform(tensor).get();
    assertTrue(Norm._2.between(sol, tensor.get(1)).number().doubleValue() < 2e-2);
  }

  public void testWeighted() {
    Tensor tensor = Tensors.of( //
        Tensors.vector(-1, 0), //
        Tensors.vector(0, 0), //
        Tensors.vector(2, 10), //
        Tensors.vector(2, -10) //
    );
    SpatialMedian spatialMedian = WeiszfeldMethod.with(Chop._10);
    Tensor weights = Tensors.vector(10, 1, 1, 1);
    Tensor sol = spatialMedian.weighted(tensor, weights.divide(Total.ofVector(weights))).get();
    assertTrue(Norm._2.between(sol, tensor.get(0)).number().doubleValue() < 2e-2);
  }

  public void testQuantity() {
    int present = 0;
    for (int count = 0; count < 10; ++count) {
      Tensor tensor = RandomVariate.of(UniformDistribution.unit(), 20, 2).map(value -> Quantity.of(value, "m"));
      SpatialMedian spatialMedian = WeiszfeldMethod.with(Chop._10);
      Optional<Tensor> optional = spatialMedian.uniform(tensor);
      if (optional.isPresent()) {
        ++present;
        Tensor weiszfeld = optional.get();
        Clip clip = Clips.interval(Quantity.of(0, "m"), Quantity.of(1, "m"));
        clip.requireInside(weiszfeld.Get(0));
        clip.requireInside(weiszfeld.Get(1));
      }
    }
    assertTrue(5 < present);
  }

  public void testNullFail() {
    AssertFail.of(() -> WeiszfeldMethod.with(null));
  }
}
