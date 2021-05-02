package ch.alpine.tensor.num;

enum BrentCycle {
  ;
  static final int[] func = { 6, 6, 0, 1, 4, 3, 3, 4, 0 };

  public static int f(int x) {
    return func[x];
  }

  public static void cycle(int x0) {
    int power = 1;
    int lam = 1;
    int tortoise = x0;
    int hare = f(x0);
    while (tortoise != hare) {
      if (power == lam) {
        tortoise = hare;
        power *= 2;
        lam = 0;
      }
      hare = f(hare);
      lam += 1;
    }
    tortoise = hare = x0;
    for (int i = 0; i < lam; ++i) {
      hare = f(hare);
    }
    int mu = 0;
    while (tortoise != hare) {
      tortoise = f(tortoise);
      hare = f(hare);
      mu += 1;
    }
    System.out.println("lam " + lam);
    System.out.println("mu  " + mu);
  }

  public static void main(String[] args) {
    cycle(2);
    cycle(7);
    cycle(4);
  }
}
