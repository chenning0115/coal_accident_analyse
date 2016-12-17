package data;

import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

public abstract class AbstractILRFilter extends SimpleBatchFilter implements ILRFilter{

	protected abstract Instances lrprocess(Instances data);
	@Override
	public LRInstances LRProcess(LRInstances _lLrInstances) {
		// TODO Auto-generated method stub
		Instances data = _lLrInstances.instances;
		Instances data2 = null;
		try {
			data2 = this.lrprocess(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LRInstances lrInstances2 = new LRInstances(data2);
		lrInstances2.setsign_attr_date(_lLrInstances.getsign_attr_date());
		lrInstances2.setsign_attr_geolat(_lLrInstances.getsign_attr_geolat());
		lrInstances2.setsign_attr_geolon(_lLrInstances.getsign_attr_lng());
		//System.out.println("nums="+data2.numInstances());
		return lrInstances2;
	}
	
	@Override
	protected Instances determineOutputFormat(Instances arg0) throws Exception {
		// TODO Auto-generated method stub
		Instances resultformat = new Instances(arg0, 0);
		return resultformat;
	}


}
