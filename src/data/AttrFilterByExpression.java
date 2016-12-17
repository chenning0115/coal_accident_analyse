package data;

import weka.core.Instances;
import weka.filters.unsupervised.instance.SubsetByExpression;

public class AttrFilterByExpression extends AbstractILRFilter {

	public LRInstances lrinstances;
	private String expression;
	public AttrFilterByExpression(LRInstances _lLrInstances,String _expression) {
		// TODO Auto-generated constructor stub
		this.lrinstances = _lLrInstances;
		this.expression = _expression;
	}

	@Override 
	protected Instances lrprocess(Instances data) {
		// TODO Auto-generated method stub
		try {
			return this.process(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	} 

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return null; 
	}

	@Override
	protected Instances process(Instances arg0) throws Exception {
		// TODO Auto-generated method stub
		SubsetByExpression subsetByExpression = new SubsetByExpression();
		subsetByExpression.setExpression(expression);
		subsetByExpression.setInputFormat(arg0);
		return subsetByExpression.useFilter(arg0, subsetByExpression);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
