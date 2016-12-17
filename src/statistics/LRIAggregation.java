package statistics;

import java.util.ArrayList;

import data.LRInstances;

public interface LRIAggregation {

	public double calculatevalue(LRInstances lrInstances,ArrayList<Integer> indexs);
}
