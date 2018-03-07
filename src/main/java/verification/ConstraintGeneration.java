package verification;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Constraint.Operator;

/**
 * Class offering static methods to generate constraints.
 * 
 * @author Fedor Smirnov
 *
 */
public class ConstraintGeneration {

	private ConstraintGeneration(){
		
	}
	
	/**
	 * Generate a constraint setting the given variable to 1.
	 * 
	 * @param variable : the variable to set
	 * @return The constraint setting the variable.
	 */
	public static Constraint activateVariable(Object variable) {
		return setVariable(variable, true);
	}
	
	/**
	 * Generate a constraint setting the given variable to 1.
	 * 
	 * @param variable : the variable to set
	 * @return The constraint setting the variable.
	 */
	public static Constraint deactivateVariable(Object variable) {
		return setVariable(variable, false);
	}
	
	/**
	 * Generate a constraint that sets the given variable to the given value.
	 * 
	 * @param variable : The variable to set
	 * @param active : TRUE => variable set to 1; FALSE => variable set to 0
	 * @return : The constraint setting the variable
	 */
	public static Constraint setVariable(Object variable, boolean active) {
		Constraint result = new Constraint(Operator.EQ, active ? 1 : 0);
		result.add(new Literal(variable, true));
		return result;
	}
	
}
