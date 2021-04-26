// code by jph
package ch.ethz.idsc.tensor.pdf;

/** continuous distribution defined over non-discrete subset of the set of real numbers
 * 
 * CauchyDistribution
 * DagumDistribution
 * EqualizingDistribution
 * ExponentialDistribution
 * FrechetDistribution
 * GompertzMakehamDistribution
 * GumbelDistribution
 * HistogramDistribution
 * LaplaceDistribution
 * LogisticDistribution
 * LogNormalDistribution
 * NormalDistribution
 * ParetoDistribution
 * RayleighDistribution
 * TrapezoidalDistribution
 * 
 * @see DiscreteDistribution */
public interface ContinuousDistribution extends Distribution, //
    CDF, PDF, InverseCDF, RandomVariateInterface, MeanInterface, VarianceInterface {
  // ---
}
