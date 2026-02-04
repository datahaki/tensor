// code by jph
package ch.alpine.tensor.ext;

/** alternative to AtomicInteger in a single thread environment
 * 
 * @apiNote not Serializable */
public class Int { // MutableInt
  private int value;

  public Int(int value) {
    this.value = value;
  }

  public Int() {
    this(0);
  }

  public int getAndIncrement() {
    return value++;
  }

  public int getAndDecrement() {
    return value--;
  }
}
