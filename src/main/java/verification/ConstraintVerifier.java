package verification;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.ContradictionException;
import org.opt4j.satdecoding.DefaultSolver;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Solver;
import org.opt4j.satdecoding.TimeoutException;
import org.opt4j.satdecoding.VarOrder;

import static org.junit.Assert.*;

/**
 * Class that is used to verify that a certain constraint set forces variables
 * to certain values.
 * 
 * @author Fedor Smirnov
 *
 */
public class ConstraintVerifier {

	// The constraint set under verification
	protected final Set<Constraint> constraints;

	/**
	 * Initializes the constraint verification by creating a new ConstraintVerifier
	 * object.
	 * 
	 * @param prevailingConstraints
	 *            The set of constraints that describe the situation that is to be
	 *            verified.
	 * 
	 */
	public ConstraintVerifier(Set<Constraint> prevailingConstraints) {
		this(new HashSet<>(), new HashSet<>(), prevailingConstraints);
	}

	/**
	 * Initializes the constraint verification by creating a new ConstraintVerifier
	 * object.
	 * 
	 * @param activatedVariables
	 *            : The set of variables which are to be set to 1
	 * @param deactivatedVariables
	 *            : The set of variables which are to be set to 0
	 * @param prevailingConstraints
	 *            : The set of constraints that describe the situation that is to be
	 *            verified.
	 */
	public ConstraintVerifier(Set<Object> activatedVariables, Set<Object> deactivatedVariables,
			Set<Constraint> prevailingConstraints) {
		this.constraints = new HashSet<>(prevailingConstraints);
		for (Object activatedVariable : activatedVariables) {
			constraints.add(ConstraintGeneration.activateVariable(activatedVariable));
		}
		for (Object deactivatedVariable : deactivatedVariables) {
			constraints.add(ConstraintGeneration.deactivateVariable(deactivatedVariable));
		}
		assertTrue("Basic constraint set already unsolvable.", areConstraintsSolvable(constraints));
	}

	/**
	 * Checks that the given variable is not encoded in the prevailing constraint
	 * set. Throws an {@link AssertionError} otherwise.
	 * 
	 * @param variable
	 *            the given encoding variable (the object, not the literal)
	 */
	public void verifyVaribleNotEncoded(Object variable) {
		Literal pos = new Literal(variable, true);
		Literal neg = new Literal(variable, false);
		for (Constraint c : constraints) {
			if (c.contains(pos) || c.contains(neg)) {
				throw new AssertionError(
						"The variable " + variable.toString() + " is contained in the constraint " + c.toString());
			}
		}
	}

	/**
	 * Adds a {@link Constraint} setting the given variable to 1. Throws an
	 * {@link AssertionError} if the new constraint leads to a contradiction with
	 * the prevailing constraints.
	 * 
	 * @param variable
	 *            the variable that is set to 1
	 */
	public void activateVariable(Object variable) {
		this.constraints.add(ConstraintGeneration.activateVariable(variable));
		assertTrue("Activating the variable" + variable.toString() + " leads to a contradiction",
				areConstraintsSolvable(constraints));
	}

	/**
	 * Adds a {@link Constraint} setting the given variable to 0. Throws an
	 * {@link AssertionError} if the new constraint leads to a contradiction with
	 * the prevailing constraints.
	 * 
	 * @param variable
	 *            the variable that is set to 0
	 */
	public void deactivateVariable(Object variable) {
		this.constraints.add(ConstraintGeneration.deactivateVariable(variable));
		assertTrue("Deactivating the variable" + variable.toString() + " leads to a contradiction",
				areConstraintsSolvable(constraints));
	}

	/**
	 * Checks whether the given constraint set is solvable.
	 * 
	 * @param constraints
	 * @return TRUE is solvable, FALSE otherwise
	 */
	public static boolean areConstraintsSolvable(Set<Constraint> constraints) {
		Solver solver = new DefaultSolver();
		for (Constraint c : constraints) {
			solver.addConstraint(c);
		}
		boolean solvable = true;
		try {
			solver.solve(new VarOrder());
		} catch (TimeoutException timeout) {
			fail("timeout");
		} catch (ContradictionException contradiction) {
			solvable = false;
		}
		return solvable;
	}

	/**
	 * Verify that at least one of the given variable objects is active with the
	 * underlying constraint set.
	 * 
	 * @param variables
	 *            the set of the variables where at least one has to be active
	 */
	public void verifyAtLeastOneActive(Set<Object> variables) {
		Set<Constraint> extendedConstraints = new HashSet<>(constraints);
		for (Object var : variables) {
			extendedConstraints.add(ConstraintGeneration.deactivateVariable(var));
		}
		String message = "The constraints are solvable although all the variables " + variables.toString()
				+ "are deactivated, the constraint set remains solvable.";
		assertFalse(message, areConstraintsSolvable(extendedConstraints));
	}

	/**
	 * Verify that the constraint set forces the given variable to 0.
	 * 
	 * @param variable
	 *            the variable to verify.
	 */
	public void verifyVariableDeactivated(Object variable) {
		verifyVariableValue(variable, false);
	}

	/**
	 * Verify that the constraint set forces the given variable to 1.
	 * 
	 * @param variable
	 *            : the variable to verify.
	 */
	public void verifyVariableActivated(Object variable) {
		verifyVariableValue(variable, true);
	}

	/**
	 * Verifies that setting the given variable to the given value does not result
	 * in a contradiction AND that setting it to the negated value causes a
	 * contradiction.
	 * 
	 * @param variable
	 *            : the variable to verify
	 * @param active
	 *            : TRUE = 1; FALSE = 0;
	 */
	protected void verifyVariableValue(Object variable, boolean active) {
		checkForContradiction(variable, active, false);
		checkForContradiction(variable, !active, true);
	}

	/**
	 * Verifies that the input variables can be set to both 0 and 1 without causing
	 * a contradiction under the current circumstances.
	 * 
	 * @param variable
	 *            the input variable
	 */
	public void verifyVariableNotFixed(Object variable) {
		checkForContradiction(variable, true, false);
		checkForContradiction(variable, false, false);
	}

	/**
	 * Check that setting the variable to the given value causes (or does not cause)
	 * a contradiction in the constraint system under verification.
	 * 
	 * @param variable
	 *            : the variable to check
	 * @param active
	 *            : TRUE => variable set to 1; FALSE => variable set to 0;
	 * @param contradictionExpected
	 *            : TRUE => contradiction has to occur; FALSE => contradiction must
	 *            not occur
	 */
	protected void checkForContradiction(Object variable, boolean active, boolean contradictionExpected) {
		Solver solver = new DefaultSolver();
		for (Constraint c : constraints) {
			solver.addConstraint(c);
		}
		solver.addConstraint(ConstraintGeneration.setVariable(variable, active));
		boolean contradictionSeen = false;
		try {
			solver.solve(new VarOrder());
		} catch (TimeoutException timeout) {
			fail("timeout");
		} catch (ContradictionException contradiction) {
			contradictionSeen = true;
		}
		String failedTestMessage = "Setting the variable " + variable + " to " + (active ? "1" : "0") + " "
				+ (contradictionExpected ? " does not cause " : " causes ") + "a contradiction. ";
		assertEquals(failedTestMessage, contradictionExpected, contradictionSeen);
	}
}
