package data;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class RemoveAttrFilter extends AbstractILRFilter {

	private LRInstances lrInstances = null;
	private String str_removeindexlist =null;
	
	public RemoveAttrFilter(LRInstances _lLrInstances,String _str_removeindexlist) {
		// TODO Auto-generated constructor stub
		this.lrInstances = _lLrInstances;
		this.str_removeindexlist = _str_removeindexlist;
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
		Remove remove = new Remove();
		remove.setAttributeIndices(str_removeindexlist);
		remove.setInputFormat(arg0);
		return remove.useFilter(arg0, remove);
	}

}
