package verification;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

public class ConstraintVerifierTest {

	class MockVariable extends Object {

	}

	@Test
	public void testUnsolvableConstraints() {
		MockVariable var = new MockVariable();
		Set<Constraint> constraints = new HashSet<>();
		constraints.add(ConstraintGeneration.activateVariable(var));
		
		boolean assertionError;
		
		assertionError = false;
		try {
			new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);
		}catch(AssertionError error) {
			assertionError = true;
		}
		assertFalse(assertionError);
		
		constraints.add(ConstraintGeneration.deactivateVariable(var));
		
		assertionError = false;
		try {
			new ConstraintVerifier(new HashSet<>(), new HashSet<>(), constraints);
		}catch(AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
	}
	
	@Test
	public void testVariableVerification() {
		MockVariable active = new MockVariable();
		MockVariable notActive = new MockVariable();
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
		MockVariable var = new MockVariable();
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