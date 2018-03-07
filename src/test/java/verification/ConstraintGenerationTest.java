package verification;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

public class ConstraintGenerationTest {

	class MockVariable extends Object {

	}

	@Test
	public void test() {
		MockVariable var = new MockVariable();

		Constraint set1 = ConstraintGeneration.activateVariable(var);
		assertEquals(Operator.EQ,set1.getOperator());
		assertEquals(1, set1.getRhs());
		assertEquals(1, set1.size());
		assertEquals(var, set1.getLiterals().iterator().next().variable());

		Constraint set0 = ConstraintGeneration.deactivateVariable(var);
		assertEquals(Operator.EQ, set0.getOperator());
		assertEquals(0, set0.getRhs());
		assertEquals(1, set0.size());
		assertEquals(var, set0.getLiterals().iterator().next().variable());
	}

}
