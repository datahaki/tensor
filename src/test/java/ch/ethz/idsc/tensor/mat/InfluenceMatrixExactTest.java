// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Random;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class InfluenceMatrixExactTest extends TestCase {
  public void testExact() throws ClassNotFoundException, IOException {
    int n = 7;
    int _m = 3;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    Tensor design = RandomVariate.of(distribution, n, _m);
    InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
    ExactTensorQ.require(influenceMatrix.matrix());
    Tensor vector = RandomVariate.of(distribution, n);
    Tensor image = influenceMatrix.image(vector);
    ExactTensorQ.require(image);
    SymmetricMatrixQ.require(influenceMatrix.matrix());
    assertEquals(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(3));
    String string = influenceMatrix.toString();
    assertTrue(string.startsWith("InfluenceMatrix"));
  }

  public void testGaussScalar() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 3;
    int prime = 7919;
    Random random = new Random();
    Tensor design = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, m);
    InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
    Tensor matrix = influenceMatrix.matrix();
    SymmetricMatrixQ.require(matrix);
    assertEquals(Total.ofVector(influenceMatrix.leverages()), GaussScalar.of(m, prime));
    Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
    Chop.NONE.requireAllZero(zeros);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixExact.class.getModifiers()));
  }
}
