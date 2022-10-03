// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.d.GeometricDistribution;

/** Quote from Mathematica:
 * <blockquote>
 * BinomialProcess is a discrete-time and discrete-state process.
 * BinomialProcess at time n is the number of events in the interval 0 to n.
 * The number of events in the interval 0 to n follows BinomialDistribution[n, p].
 * The times between events are independent and follow GeometricDistribution[p].
 * </blockquote>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BinomialProcess.html">BinomialProcess</a> */
public enum BinomialProcess {
  ;
  public static RandomProcess of(Scalar p) {
    // FIXME TENSOR need to add one!
    return RenewalProcess.of(GeometricDistribution.of(p));
  }
}
