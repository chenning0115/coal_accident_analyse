 package data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;

import au.com.objectix.jgridshift.Util;
import data.spacefilter.LRISpaceFilter;
import data.spacefilter.ShapfileSpaceFilterImpl;
import jj2000.j2k.fileformat.reader.FileFormatReader;
import statistics.timeserise.TimeSeriseStatistics;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;
import weka.gui.streams.InstanceJoiner;

public class SpaceFilter extends AbstractILRFilter{

	private LRInstances lrInstances;
	private String CQL;
	public SpaceFilter(LRInstances _lrInstances)
	{ 
		this.lrInstances = _lrInstances;
	}
	
	public void setCQL(String _CQL)
	{
		this.CQL = _CQL;
	} 

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return "A filter used to filter instances which satisfy position given by user";
	}

	@Override
	protected Instances process(Instances arg0) throws Exception {
		// TODO Auto-generated method stub
		Instances result = new Instances(determineOutputFormat(arg0), 0);
		Instances data = lrInstances.instances;
		//现在还是要调用一次读入一次shapefile的情况
		LRISpaceFilter lrfilter = ShapfileSpaceFilterImpl.GetInstanceofShapfileSpaceFilterImple(Utils.shp_china);
		//lrfilter.setSourceInstances(Utils.points2featuers(lrInstances));
		ArrayList<Integer> indexs = lrfilter.spaceFilterbyCQL(Utils.points2featuers(lrInstances),CQL);
		for (Integer index : indexs) {
			result.add(data.get(index));
		}
		return result;
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			LRInstances lrInstances = LRInstances.loadfromDB("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin", "select * from coalmine");
			lrInstances.setsign_attr_date(3);
			lrInstances.setsign_attr_geolon(1);
			lrInstances.setsign_attr_geolat(2);
			/*TimeSeriseStatistics statistics = new TimeSeriseStatistics(lrInstances);
			double[] result = statistics.StatisticAverage(5);
			int sum = 0;
			for(int i=0;i<result.length;i++)
			{
				sum+=result[i];
				System.out.println(result[i]+",");
				
			}
			System.out.println("sum="+sum);*/
			
			SpaceFilter df = new SpaceFilter(lrInstances);
			df.setCQL("NAME_1='Guizhou'");
			LRInstances lrInstances2 = df.LRProcess(lrInstances);
			System.out.println("size="+lrInstances2.instances.numInstances());
			LRISpaceFilter lriSpaceFilter =  ShapfileSpaceFilterImpl.GetInstanceofShapfileSpaceFilterImple(Utils.shp_china);
			HashMap<Object, ArrayList<Integer>> map = lriSpaceFilter.spaceDividedbyAttribute(Utils.points2featuers(lrInstances), "NAME_1");
			Set<Object> oset = map.keySet();
			int sum = 0;
			for (Object object : oset) {
				int temp = map.get(object).size();
				System.out.println(object.toString());
				sum+=temp;
			}
			System.out.println("sum="+sum);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
}
