package statistics;

import java.util.ArrayList;

import data.LRInstances;

public class DefaultLRAggregation implements LRIAggregation{

	@Override
	public double calculatevalue(LRInstances lrInstances, ArrayList<Integer> indexs) {
		// TODO Auto-generated method stub
		return (double)indexs.size();
	}

}
