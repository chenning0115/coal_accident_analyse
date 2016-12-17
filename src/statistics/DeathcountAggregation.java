package statistics;

import java.util.ArrayList;

import data.LRInstances;
import weka.core.Instance;

public class DeathcountAggregation implements LRIAggregation {

	private int countindex = 0;
	public DeathcountAggregation(int _countindex) {
		// TODO Auto-generated constructor stub
		this.countindex = _countindex;
	}
	@Override
	public double calculatevalue(LRInstances lrInstances, ArrayList<Integer> indexs) {
		// TODO Auto-generated method stub
		int sum = 0;
		for (Integer index: indexs) {
			sum+=lrInstances.getValue(index,countindex);
		}
		return sum;
	}

}
