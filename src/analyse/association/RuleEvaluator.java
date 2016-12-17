package analyse.association;

import weka.associations.AssociationRule;

public interface RuleEvaluator {
	public boolean evaluate(AssociationRule rule);

}
