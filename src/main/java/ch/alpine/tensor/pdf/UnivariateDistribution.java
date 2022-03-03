// code by jph
package ch.alpine.tensor.pdf;

/** common base class for {@link ContinuousDistribution} and {@link DiscreteDistribution} */
public interface UnivariateDistribution extends Distribution, //
    PDF, CDF, InverseCDF, RandomVariateInterface, MeanInterface, VarianceInterface {
  // ---
}
