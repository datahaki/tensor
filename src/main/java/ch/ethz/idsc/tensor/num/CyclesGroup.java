// code by jph
package ch.ethz.idsc.tensor.num;

/* package */ enum CyclesGroup implements GroupInterface<Cycles> {
  INSTANCE;

  @Override // from GroupInterface
  public Cycles identity() {
    return Cycles.identity();
  }

  @Override // from GroupInterface
  public Cycles invert(Cycles cycles) {
    return cycles.inverse();
  }

  @Override // from GroupInterface
  public Cycles combine(Cycles cycles1, Cycles cycles2) {
    return cycles1.combine(cycles2);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s", getClass().getSimpleName());
  }
}
