package analyse.association;

import opendap.util.iniFile;
import weka.associations.AssociationRule;

public class ItemnumEvaluator implements RuleEvaluator {

	int premisnum = 1;
	int sequencenum = 1;
	
	public ItemnumEvaluator(int _premisnum,int _sequencenum) {
		// TODO Auto-generated constructor stub
		this.premisnum = _premisnum;
		this.sequencenum = _sequencenum;
	}
	@Override
	public boolean evaluate(AssociationRule rule) {
		// TODO Auto-generated method stub
		if(rule.getPremise().size()==premisnum&&rule.getConsequence().size()==sequencenum)
		return true;
		return false;
	}

}
