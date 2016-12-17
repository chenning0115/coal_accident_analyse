package data;

import java.util.ArrayList;

import opendap.dap.test.expr_test;
import statistics.AttributeBasedCount;
import statistics.timeserise.TimeSeriseStatistics;
import weka.core.Instance;
import weka.core.Instances;

public class AttributeFilter extends AbstractILRFilter {

	LRInstances lrinstance = null;
	int indexofattribute =0;
	Object value = null;
	
	public AttributeFilter(LRInstances _lLrInstances,int _indexofattribute,Object _value) {
		// TODO Auto-generated constructor stub
		this.lrinstance = _lLrInstances;
		this.indexofattribute = _indexofattribute;
		this.value = _value;
	}
	@Override
	protected Instances lrprocess(Instances data) {
		// TODO Auto-generated method stub
		try {
			return this.process(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Instances process(Instances arg0) throws Exception {
		// TODO Auto-generated method stub
		Instances result = new Instances(determineOutputFormat(arg0), 0);
		Instances data = lrinstance.instances;
		double val = this.lrinstance.ConvertValue2Double(indexofattribute, value);
		
		for(int i=0;i<data.numInstances();i++)
		{
			Instance ins = data.get(i);
			//System.out.println(ins.value(indexofattribute)+" "+val);
			if(ins.value(indexofattribute)==val)
			{
				result.add(ins);
			}
		}
		return result;
		
	}

	public static void main(String[] args)
	{
		try{
		LRInstances _lrInstances = LRInstances.loadfromDB("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin", "select * from coalmineaccidents");
		_lrInstances.setsign_attr_date(3);
		_lrInstances.setsign_attr_geolon(1);
		_lrInstances.setsign_attr_geolat(2);
		/*SpaceFilter spaceFilter = new SpaceFilter(_lrInstances);
		spaceFilter.setCQL("NAME_1='Guizhou'");
		LRInstances ins_spacefiltered = spaceFilter.LRProcess(_lrInstances);
		//System.out.println("spaced="+ins_spacefiltered.getsign_attr_date());
		//filter instances by every type
		String[] typenames = new String[]{"dingban","shuizai","huozai","wasi","fangpao","jidian","yunshu","qita"};
		LRInstances[] ins_typefiltered = new LRInstances[typenames.length];
		double[][] table = new double[typenames.length][];
		for(int i =0;i<typenames.length;i++)
		{
			AttributeFilter attributeFilter = new AttributeFilter(ins_spacefiltered, 6,typenames[i]);
			ins_typefiltered[i] = attributeFilter.LRProcess(ins_spacefiltered);
			
			TimeSeriseStatistics timesta = new TimeSeriseStatistics(ins_typefiltered[i]);
			table[i] = timesta.Statisticbyinter(TimeSeriseStatistics.LRMonth);
		} 
		
		for(int i =0;i<typenames.length;i++)
		{
			for(int j=0;j<table[i].length;j++)
				System.out.print(table[i][j]+",");
			System.out.println();
		}*/
		SpaceFilter spaceFilter = new SpaceFilter(_lrInstances);
		spaceFilter.setCQL("NAME_1='Guizhou'");
		LRInstances ins_spacefiltered = spaceFilter.LRProcess(_lrInstances);
		//System.out.println("spaced="+ins_spacefiltered.getsign_attr_date());
		//filter instances by every type
		String[] typenames = new String[]{"dingban","shuizai","huozai","wasi","fangpao","jidian","yunshu","qita"};
		double[][] table = new double[typenames.length][];
		for(int i =0;i<typenames.length;i++)
		{
			TimeSeriseStatistics timesta = new TimeSeriseStatistics(ins_spacefiltered);
			timesta.SetLRIAggregation(new AttributeBasedCount(6,typenames[i]));
			table[i] = timesta.Statisticbyinter(TimeSeriseStatistics.LRMonth);
		} 
		for(int i=0;i<typenames.length;i++)
		{
			System.out.println(typenames[i]+" "+table[i].length);
		}
		for(int i=0;i<typenames.length;i++)
		{
			for(int j=0;j<table[i].length;j++)
			{
				System.out.print(table[i][j]+" ");
			}
			System.out.println();
		}
	}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
