// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.ClipsBijection;

/** EXPERIMENTAL
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PolynomialQ.html">PolynomialQ</a> */
public enum PolynomialQ {
  ;
  /** @param suo
   * @param clip
   * @param degree
   * @param chop
   * @return */
  public static boolean of(ScalarUnaryOperator suo, Clip clip, int degree, Chop chop) {
    ClipsBijection clipsBijection = new ClipsBijection(Clips.absoluteOne(), clip);
    ScalarUnaryOperator ref = clipsBijection.forward().andThen(suo);
    ScalarUnaryOperator apx = ChebyshevInterpolation.of(ref, degree + 1);
    return RandomVariate.stream(UniformDistribution.of(Clips.absoluteOne())) //
        .limit(2 * degree) //
        .allMatch(s -> chop.isClose(ref.apply(s), apx.apply(s)));
  }

  /** @param suo
   * @param clip
   * @param degree at most
   * @return */
  public static boolean of(ScalarUnaryOperator suo, Clip clip, int degree) {
    return of(suo, clip, degree, Tolerance.CHOP);
  }
}
