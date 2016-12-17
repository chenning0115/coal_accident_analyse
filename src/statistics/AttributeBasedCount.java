package statistics;

import java.util.ArrayList;

import data.LRInstances;
import weka.core.Instance;
import weka.core.Instances;

public class AttributeBasedCount implements LRIAggregation{

	private int indexofattribute = 0;
	private Object value;
	
	public AttributeBasedCount(int _indexofattribute,Object _value) {
		// TODO Auto-generated constructor stub
		this.indexofattribute = _indexofattribute;
		this.value = _value;
	}
	
	@Override
	public double calculatevalue(LRInstances lrInstances, ArrayList<Integer> indexs) {
		// TODO Auto-generated method stub
		int count = 0;
		try {
			double val = lrInstances.ConvertValue2Double(indexofattribute, value);
			Instances data = lrInstances.instances;
			for(int i=0;i<indexs.size();i++)
			{
				Instance ins = data.get(i);
				if(ins.value(indexofattribute)==val)
				{
					count++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

}
