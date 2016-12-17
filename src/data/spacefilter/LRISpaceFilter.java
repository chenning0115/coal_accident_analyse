package data.spacefilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import weka.core.Instances;

public interface LRISpaceFilter {

	/*
	 * 输入一个查询语句，返回与该查询语句对应的空间对象相交的instace索引集
	 * 该查询语句对应输入shp文件的属性与属性值
	 * 每个instance对象在空间中是一个点对象
	 */
	
	public ArrayList<Integer> spaceFilterbyCQL(List<SimpleFeature> _list_points,String CQL);
	public HashSet<String> getDistinctAttributenames();
	public HashSet<Object> getDistinctAttributeValues(String attributename);
	public HashMap<Object, ArrayList<Integer>> spaceDividedbyAttribute(List<SimpleFeature> _list_points,String attritudename);
	
}
