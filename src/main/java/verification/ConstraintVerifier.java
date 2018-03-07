package verification;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.ContradictionException;
import org.opt4j.satdecoding.DefaultSolver;
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

	// The constraint set contains the information about the initialization of the
	// variables
	protected final Set<Constraint> constraints;

	/**
	 * Initialize the constraint verification by creating a new ConstraintVerifier
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
	 * Check whether the given constraint set is solvable.
	 * 
	 * @param constraints
	 * @return TRUE is solvable, FALSE otherwise
	 */
	protected boolean areConstraintsSolvable(Set<Constraint> constraints) {
		Solver solver = new DefaultSolver();
		for(Constraint c : constraints) {
			solver.addConstraint(c);
		}
		boolean solvable = true;
		try {
			solver.solve(new VarOrder());
		}catch(TimeoutException timeout) {
			fail("timeout");
		}catch(ContradictionException contradiction) {
			solvable = false;
		}
		return solvable;
	}

	/**
	 * Verify that the constraint set forces the given variable to 0.
	 * 
	 * @param variable
	 *            : the variable to verify.
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
