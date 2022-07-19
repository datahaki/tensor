package ch.alpine.tensor.opt.qh3;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** Simple example usage of QuickHull3D. Run as the command
 * <pre>
 * java quickhull3d.SimpleExample
 * </pre> */
class SimpleExampleTest {
  /** Run for a simple demonstration of QuickHull3D. */
  @Test
  void test() {
    // x y z coordinates of 6 points
    Tensor points = Tensors.of( //
        Tensors.vector(0.0, 0.0, 0.0), //
        Tensors.vector(1.0, 0.5, 0.0), //
        Tensors.vector(2.0, 0.0, 0.0), //
        Tensors.vector(0.5, 0.5, 0.5), //
        Tensors.vector(0.0, 0.0, 2.0), //
        Tensors.vector(0.1, 0.2, 0.3), //
        Tensors.vector(0.0, 2.0, 0.0));
    QuickHull3D hull = new QuickHull3D();
    hull.build(points);
    System.out.println("Vertices:");
    Vector3d[] vertices = hull.getVertices();
    for (int i = 0; i < vertices.length; i++) {
      Vector3d pnt = vertices[i];
      System.out.println(pnt.x + " " + pnt.y + " " + pnt.z);
    }
    System.out.println("Faces:");
    int[][] faceIndices = hull.getFaces();
    for (int i = 0; i < vertices.length; i++) {
      for (int k = 0; k < faceIndices[i].length; k++) {
        System.out.print(faceIndices[i][k] + " ");
      }
      System.out.println("");
    }
  }
}
