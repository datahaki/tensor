// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testRequireUnit() {
    Scalar scalar = RealScalar.of(1.0 + 1e-13);
    Scalar mapped = StaticHelper.requireUnit(scalar);
    Clips.unit().requireInside(mapped);
  }

  public void testRequireUnitFail() {
    Scalar scalar = RealScalar.of(1.0 + 1e-5);
    AssertFail.of(() -> StaticHelper.requireUnit(scalar));
  }

  public void testUnitizeChop() {
    assertEquals(StaticHelper.unitize_chop(RealScalar.of(1e-13)), RealScalar.ZERO);
    assertEquals(StaticHelper.unitize_chop(RealScalar.of(1e-11)), RealScalar.ONE);
    assertEquals(StaticHelper.unitize_chop(RealScalar.of(123)), RealScalar.ONE);
  }

  public static boolean check(SingularValueDecomposition svd, Tensor matrix) {
    Tensor u = svd.getU();
    Tensor v = svd.getV();
    Tensor c = Dot.of(u, DiagonalMatrix.with(svd.values()), Transpose.of(v));
    return Tolerance.CHOP.isClose(c, matrix);
  }

  public void testSvdWithQuantity() {
    int n = 4;
    int _m = 3;
    Distribution distribution = DiscreteUniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count)
      for (int r = 1; r < _m - 1; ++r) {
        Tensor m1 = RandomVariate.of(distribution, n, r).map(s -> Quantity.of(s, "m"));
        Tensor m2 = RandomVariate.of(distribution, r, _m);
        Tensor design = m1.dot(m2);
        ExactTensorQ.require(design);
        SingularValueDecomposition svd = SingularValueDecomposition.of(design);
        // System.out.println("u=" + svd.getU());
        boolean hasQuantity = svd.getU().flatten(-1).anyMatch(s -> s instanceof Quantity);
        if (hasQuantity)
          throw TensorRuntimeException.of(design);
        InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
        // FIXME 0.0[m] != 0
        SymmetricMatrixQ.require(influenceMatrix.matrix());
        influenceMatrix.residualMaker();
        Tensor vector = RandomVariate.of(distribution, n);
        Tensor image = influenceMatrix.image(vector);
        VectorQ.requireLength(image, vector.length());
        Chop._10.requireClose(Total.ofVector(influenceMatrix.leverages()), RealScalar.of(r));
        String string = influenceMatrix.toString();
        assertTrue(string.startsWith("InfluenceMatrix["));
      }
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
