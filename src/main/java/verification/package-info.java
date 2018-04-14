/**
 * Package with classes used to verify that a constraint set forces variables to
 * a certain assignment. The {@link verification.ConstraintGeneration} contains
 * static methods for a convenient creation of
 * {@link org.opt4j.satdecoding.Constraint}s that are used throughout the
 * verification. The {@link verification.ConstraintVerifier} is used for the
 * actual verification. Creating an instance of this object within a Junit test
 * allows to test whether the constraint set under verification results in
 * setting the encoded variables to 1/0, or actually does not fix their value at
 * all.
 * 
 */
package verification;