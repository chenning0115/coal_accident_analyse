package analyse.association;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.xsd.impl.XSDNamedComponentImpl.StringPairComparator;

import data.AttrFilterByExpression;
import data.LRInstances;
import data.RemoveAttrFilter;
import java_cup.runtime.lr_parser;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.SelectedTag;

public class LRAssociation {

	private LRInstances lrInstances=null;
	double UpperBoundMinSupport;
	double LowerBoundMinSupport;
	double delt;
	public final static int Metrictype_lift = 1;
	public final static int Metrictyoe_confidence = 0;
	public final static int Metrictype_leverage = 2;
	public final static int Metrictype_conviction = 3;
	SelectedTag Metrictype;
	double minmetric;
	int numrules;
	
	private RuleEvaluator ruleEvaluator = null;
	public static  SelectedTag Metrictypeconvert2tag(int sign)
	{
		return new SelectedTag(sign,Apriori.TAGS_SELECTION);
	}
	
	public LRAssociation(LRInstances _lLrInstances,double _UpperBoundSupport,double _LowerBoundMinSupport
			,double _delt,int _metrictype,double _minmetric,int _numrules)
	{
		this.lrInstances = _lLrInstances;
		this.UpperBoundMinSupport = _UpperBoundSupport;
		this.LowerBoundMinSupport = _LowerBoundMinSupport;
		this.delt = _delt;
		this.Metrictype = Metrictypeconvert2tag(_metrictype);
		this.minmetric = _minmetric;
		this.numrules = _numrules;
	}
	public void setRuleEvalator(RuleEvaluator _ruleEvaluator)
	{
		this.ruleEvaluator = _ruleEvaluator;
	}
	
	private ArrayList<LRRule> conver2LRRule(AssociationRules associationRules)
	{
		java.util.List<AssociationRule> ori_rules = associationRules.getRules();
		java.util.List<AssociationRule> rules = new ArrayList<>();
		for (AssociationRule temprule : ori_rules) {
			if(!rules.contains(temprule))
			{
				rules.add(temprule);
			}
		}
		//System.out.println("ori = "+ori_rules.size()+"processed = "+rules.size());
		ArrayList<LRRule> resultlist = new ArrayList<>();
		for (AssociationRule rule : rules) {
			try{
				HashMap<String,Double> tempmap = new HashMap<>();
				String[] strs = rule.getMetricNamesForRule();
				for(int i=0;i<strs.length;i++)
				{
					tempmap.put(strs[i], rule.getNamedMetricValue(strs[i]));
					Collection<Item> col_pre = rule.getPremise();	
				}
				LRRule rule2 = new LRRule(tempmap, rule.getPremise().toString(),rule.getConsequence().toString());
				resultlist.add(rule2);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//System.out.println("here_1 = "+resultlist.size());
		return resultlist;
	}
	
	public ArrayList<LRRule> runapriori()
	{
		Apriori apriori = new Apriori();
		apriori.setUpperBoundMinSupport(UpperBoundMinSupport);
		apriori.setLowerBoundMinSupport(LowerBoundMinSupport);
		apriori.setDelta(delt);
		apriori.setMetricType(Metrictype);
		apriori.setMinMetric(minmetric);
		apriori.setNumRules(numrules);
		try {
			apriori.buildAssociations(lrInstances.instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssociationRules preresult = null;
		AssociationRules associationRules = apriori.getAssociationRules();
		if(ruleEvaluator==null) preresult = associationRules;
		else{
			java.util.List<AssociationRule> rules = associationRules.getRules(); 
			ArrayList<AssociationRule> resultrules = new ArrayList<>();
			for (AssociationRule rule : rules) {
				if(ruleEvaluator.evaluate(rule))
				{
					resultrules.add(rule);
				}
			}
			preresult = new AssociationRules(resultrules);
		}
		//System.out.println("here_0 = "+preresult.getNumRules());
		return conver2LRRule(preresult);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			LRInstances lrInstances = LRInstances.loadfromfile("resourse/test.csv");
			AttrFilterByExpression attrFilterByExpression = new AttrFilterByExpression(lrInstances,
					"((ATT3 is 'veryserious') or (ATT3 is 'serious'))");
			LRInstances lrInstances2 = attrFilterByExpression.LRProcess(lrInstances);
			RemoveAttrFilter removeAttrFilter = new RemoveAttrFilter(lrInstances2, "1,3,5");
			LRInstances lrInstances3 = removeAttrFilter.LRProcess(lrInstances2);
			LRAssociation lrAssociation = new LRAssociation(lrInstances3, 1.0, 0.1, 0.01, LRAssociation.Metrictyoe_confidence, 0.01, 1000);
			//RuleEvaluator evaluator = new ItemnumEvaluator(2, 1);
			//lrAssociation.setRuleEvalator(evaluator);
			ArrayList<LRRule> list = lrAssociation.runapriori();
			
			for (LRRule rule : list) {
				System.out.println(rule);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
