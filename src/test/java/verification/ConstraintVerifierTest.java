package verification;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

public class ConstraintVerifierTest {

	public void testVariableNotFixed() {
		Object var = new Object();
		Set<Constraint> constraints = new HashSet<>();
		boolean assertionError = false;
		try {
			ConstraintVerifier verifier = new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);
			verifier.verifyVariableNotFixed(var);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertFalse(assertionError);
		assertionError = false;
		try {
			Set<Object> active = new HashSet<>();
			active.add(var);
			ConstraintVerifier verifier = new ConstraintVerifier(active, new HashSet<>(), constraints);
			verifier.verifyVariableNotFixed(var);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
		assertionError = false;
		try {
			Set<Object> unactive = new HashSet<>();
			unactive.add(var);
			ConstraintVerifier verifier = new ConstraintVerifier(new HashSet<>(), unactive, constraints);
			verifier.verifyVariableNotFixed(var);
		} catch (AssertionError error) {
			assertionError = true;
		}
	}

	public void testUnsolvableConstraints() {
		Object var = new Object();
		Set<Constraint> constraints = new HashSet<>();
		constraints.add(ConstraintGeneration.activateVariable(var));

		boolean assertionError;

		assertionError = false;
		try {
			new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertFalse(assertionError);

		constraints.add(ConstraintGeneration.deactivateVariable(var));

		assertionError = false;
		try {
			new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
	}

	@Test
	public void testVariableVerification() {
		Object active = new Object();
		Object notActive = new Object();
		Set<Object> activeVars = new HashSet<>();
		Set<Object> unactiveVars = new HashSet<>();
		activeVars.add(active);
		unactiveVars.add(notActive);
		ConstraintVerifier verifier = new ConstraintVerifier(activeVars, unactiveVars, new HashSet<>());

		boolean assertionException;

		assertionException = false;
		try {
			verifier.verifyVariableActivated(active);
		} catch (AssertionError error) {
			assertionException = true;
		}
		assertFalse(assertionException);

		assertionException = false;
		try {
			verifier.verifyVariableActivated(notActive);
		} catch (AssertionError error) {
			assertionException = true;
		}
		assertTrue(assertionException);

		assertionException = false;
		try {
			verifier.verifyVariableDeactivated(active);
		} catch (AssertionError error) {
			assertionException = true;
		}
		assertTrue(assertionException);

		assertionException = false;
		try {
			verifier.verifyVariableDeactivated(notActive);
		} catch (AssertionError error) {
			assertionException = true;
		}
		assertFalse(assertionException);
	}

	@Test
	public void testContradictionCheck() {
		Object var = new Object();
		Constraint active = ConstraintGeneration.activateVariable(var);
		Set<Constraint> constraints = new HashSet<>();
		constraints.add(active);
		ConstraintVerifier verifier = new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);

		boolean assertionException = false;
		try {
			verifier.checkForContradiction(var, true, false);
		} catch (AssertionError assertionError) {
			assertionException = true;
		}
		assertFalse(assertionException);

		assertionException = false;
		try {
			verifier.checkForContradiction(var, false, true);
		} catch (AssertionError assertionError) {
			assertionException = true;
		}
		assertFalse(assertionException);

		assertionException = false;
		try {
			verifier.checkForContradiction(var, false, false);
		} catch (AssertionError assertionError) {
			assertionException = true;
		}
		assertTrue(assertionException);

		assertionException = false;
		try {
			verifier.checkForContradiction(var, true, true);
		} catch (AssertionError assertionError) {
			assertionException = true;
		}
		assertTrue(assertionException);
	}
}
