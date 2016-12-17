package statistics.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.kerberos.KerberosTicket;

import org.omg.PortableServer.THREAD_POLICY_ID;

import data.LRInstances;
import data.Utils;
import data.spacefilter.LRISpaceFilter;
import data.spacefilter.ShapfileSpaceFilterImpl;
import statistics.DefaultLRAggregation;
import statistics.LRIAggregation;

/*
 * 本类用于，根据用户指定的空间数据的属性，获取该属性对应的所有不同属性值，对于每一种属性值，获取与该属性值对应的空间对象相交的所有instance
 * 本类用于对不同地区的数据进行统计，例如统计全国各个省的煤炭事故总量等
 * 本类目前还没有抽象成任何接口形式，待日后关于空间统计的需求更加详细之后可以进行抽象，当前只支持上述
 */
public class SpaceStatistics {
	
	private LRISpaceFilter lriSpaceFilter = null;
	private HashSet<String> attributenames = null;
	public LRInstances lrInstances =null;
	public LRIAggregation lriAggregation = null;
	public SpaceStatistics(String shppath,LRInstances _lrInstances)
	{
		this.lrInstances = _lrInstances;
		this.lriSpaceFilter = ShapfileSpaceFilterImpl.GetInstanceofShapfileSpaceFilterImple(shppath);
		this.lriAggregation = new DefaultLRAggregation();
	}
	
	public HashSet<String> getAttributenames()
	{
		if(attributenames==null)
		{
			this.attributenames = this.lriSpaceFilter.getDistinctAttributenames();
		}
		return this.attributenames;
	}
	
	public static class tuple
	{
		Object attributevalue;
		double value;
		public tuple(Object _attributevalue,double _value)
		{
			this.attributevalue = _attributevalue;
			this.value = _value;
		}
	}
	
	public tuple[] statisticsbyspace(String attributename)throws Exception
	{
		HashSet<String> attributenameset = getAttributenames();
		if(!attributenameset.contains(attributename))
		{
			throw new Exception("The attribute name is not in space data");
		}
		HashMap<Object, ArrayList<Integer>> map = this.lriSpaceFilter.spaceDividedbyAttribute(Utils.points2featuers(lrInstances),attributename);
		Set<Object> keSet = map.keySet();
		tuple[] resultlist = new tuple[keSet.size()];
		int i = 0;
		for (Object key : keSet) {
			ArrayList<Integer> tempindexs = map.get(key);
			//System.out.println(this.lriAggregation.calculatevalue(lrInstances, tempindexs));
			tuple temptuple = new tuple(key, this.lriAggregation.calculatevalue(lrInstances, tempindexs));
			resultlist[i] = temptuple;
			i++;
		}
		return resultlist;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LRInstances lrInstances;
		try {
			lrInstances = LRInstances.loadfromDB("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres", "select * from coalmine");
			lrInstances.setsign_attr_date(3);
			lrInstances.setsign_attr_geolon(1);
			lrInstances.setsign_attr_geolat(2);
			SpaceStatistics spaceStatistics = new SpaceStatistics(Utils.shp_china, lrInstances);
			Set<String> names = spaceStatistics.getAttributenames();
			for (String string : names) {
				System.out.println(string);
			}
			tuple[] results = spaceStatistics.statisticsbyspace("NAME_1");
			for(int i = 0;i<results.length;i++)
			{
				System.out.println(results[i].attributevalue+"="+results[i].value);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
