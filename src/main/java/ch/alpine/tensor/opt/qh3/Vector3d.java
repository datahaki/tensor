/** Copyright John E. Lloyd, 2004. All rights reserved. Permission to use,
 * copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 *
 * This software is distributed "as is", without any warranty, including
 * any implied warranty of merchantability or fitness for a particular
 * use. The author assumes no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software. */
package ch.alpine.tensor.opt.qh3;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** A three-element vector. This class is actually a reduced version of the
 * Vector3d class contained in the author's matlib package (which was partly
 * inspired by javax.vecmath). Only a mininal number of methods
 * which are relevant to convex hull generation are supplied here.
 *
 * @author John E. Lloyd, Fall 2004 */
public class Vector3d {
  /** Precision of a double. */
  static private final double DOUBLE_PREC = 2.2204460492503131e-16;
  /** First element */
  public Tensor xyz;

  /** Creates a 3-vector and initializes its elements to 0. */
  public Vector3d() {
  }

  /** Creates a 3-vector by copying an existing one.
   *
   * @param v vector to be copied */
  public Vector3d(Vector3d v) {
    set(v);
  }

  /** Creates a 3-vector with the supplied element values.
   *
   * @param x first element
   * @param y second element
   * @param z third element */
  public Vector3d(Scalar x, Scalar y, Scalar z) {
    set(x, y, z);
  }

  public Vector3d(Number x, Number y, Number z) {
    this(RealScalar.of(x), RealScalar.of(y), RealScalar.of(z));
  }

  /** Gets a single element of this vector.
   * Elements 0, 1, and 2 correspond to x, y, and z.
   *
   * @param i element index
   * @return element value throws ArrayIndexOutOfBoundsException
   * if i is not in the range 0 to 2. */
  public Scalar get(int i) {
    return xyz.Get(i);
  }

  /** Sets a single element of this vector.
   * Elements 0, 1, and 2 correspond to x, y, and z.
   *
   * @param i element index
   * @param value element value
   * @return element value throws ArrayIndexOutOfBoundsException
   * if i is not in the range 0 to 2. */
  public void set(int i, Scalar value) {
    if (xyz == null)
      xyz = Array.zeros(3);
    xyz.set(value, i);
  }

  /** Sets the values of this vector to those of v1.
   *
   * @param v1 vector whose values are copied */
  public void set(Vector3d v1) {
    set(v1.xyz);
  }

  /** Adds vector v1 to v2 and places the result in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector */
  public void add(Vector3d v1, Vector3d v2) {
    set(v1.xyz.add(v2.xyz));
  }

  /** Adds this vector to v1 and places the result in this vector.
   *
   * @param v1 right-hand vector */
  public void add(Vector3d v1) {
    set(xyz.add(v1.xyz));
  }

  /** Subtracts vector v1 from v2 and places the result in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector */
  public void sub(Vector3d v1, Vector3d v2) {
    set(v1.xyz.subtract(v2.xyz));
  }

  /** Subtracts v1 from this vector and places the result in this vector.
   *
   * @param v1 right-hand vector */
  public void sub(Vector3d v1) {
    set(xyz.subtract(v1.xyz));
  }

  /** Scales the elements of this vector by <code>s</code>.
   *
   * @param s scaling factor */
  public void scale(Scalar s) {
    set(xyz.multiply(s));
  }

  /** Scales the elements of vector v1 by <code>s</code> and places
   * the results in this vector.
   *
   * @param s scaling factor
   * @param v1 vector to be scaled */
  public void scale(Scalar s, Vector3d v1) {
    set(v1.xyz.multiply(s));
  }

  /** Returns the 2 norm of this vector. This is the square root of the
   * sum of the squares of the elements.
   *
   * @return vector 2 norm */
  public Scalar norm() {
    return Vector2Norm.of(xyz);
  }

  /** Returns the square of the 2 norm of this vector. This
   * is the sum of the squares of the elements.
   *
   * @return square of the 2 norm */
  public Scalar normSquared() {
    return Vector2NormSquared.of(xyz);
  }

  /** Returns the Euclidean distance between this vector and vector v.
   *
   * @return distance between this vector and v */
  public Scalar distance(Vector3d v) {
    return Vector2Norm.between(xyz, v.xyz);
  }

  /** Returns the squared of the Euclidean distance between this vector
   * and vector v.
   *
   * @return squared distance between this vector and v */
  public Scalar distanceSquared(Vector3d v) {
    return Vector2NormSquared.between(xyz, v.xyz);
  }

  /** Returns the dot product of this vector and v1.
   *
   * @param v1 right-hand vector
   * @return dot product */
  public Scalar dot(Vector3d v1) {
    return (Scalar) xyz.dot(v1.xyz);
  }

  /** Normalizes this vector in place. */
  public void normalize() {
    Scalar scalar = Vector2NormSquared.of(xyz);
    double lenSqr = scalar.number().doubleValue();
    double err = lenSqr - 1;
    if (err > (2 * DOUBLE_PREC) || err < -(2 * DOUBLE_PREC)) {
      double len = Math.sqrt(lenSqr);
      set(xyz.divide(RealScalar.of(len)));
    }
  }

  /** Sets the elements of this vector to zero. */
  public void setZero() {
    set(Array.zeros(3));
  }

  /** Sets the elements of this vector to the prescribed values.
   * 
   * @param x value for first element
   * @param y value for second element
   * @param z value for third element */
  public void set(Scalar x, Scalar y, Scalar z) {
    xyz = Tensors.of(x, y, z);
  }

  /** Computes the cross product of v1 and v2 and places the result
   * in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector */
  public void cross(Vector3d v1, Vector3d v2) {
    set(Cross.of(v1.xyz, v2.xyz));
  }

  private void set(Tensor tensor) {
    xyz = tensor;
  }

  /** Sets the elements of this vector to uniformly distributed
   * random values in a specified range, using a supplied
   * random number generator.
   *
   * @param lower lower random value (inclusive)
   * @param upper upper random value (exclusive)
   * @param generator random number generator */
  protected void setRandom(Scalar lower, Scalar upper, Random generator) {
    Distribution distribution = UniformDistribution.of(lower, upper);
    set(RandomVariate.of(distribution, generator, 3));
  }

  /** Returns a string representation of this vector, consisting
   * of the x, y, and z coordinates.
   *
   * @return string representation */
  @Override
  public String toString() {
    return xyz.toString();
  }
}
