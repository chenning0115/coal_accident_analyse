package data.spacefilter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.w3.xlink.Simple;

//import org.opengis.geometry.Geometry;
import com.vividsolutions.jts.geom.Geometry;
import data.LRInstances;
import data.Utils;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

public abstract class AbstractSpaceFilter implements LRISpaceFilter {

	protected SimpleFeatureSource source;
	public abstract void setFeatureSource();
	@Override
	public ArrayList<Integer> spaceFilterbyCQL(List<SimpleFeature> _list_points,String CQL) {
		// TODO Auto-generated method stub
		//ArrayList<SimpleFeature> list_points = Utils.points2featuers(lrInstances);
		ArrayList<SimpleFeature> list_points_result = new ArrayList<>();
		FilterFactory2 factory2 = CommonFactoryFinder.getFilterFactory2();
		try {
			Filter filter = org.geotools.filter.text.cql2.CQL.toFilter(CQL);
			SimpleFeatureCollection mapfeatures = source.getFeatures(filter);
			String geometrylocalname = mapfeatures.getSchema().getGeometryDescriptor().getLocalName();
			SimpleFeatureIterator iterator = mapfeatures.features();
			while(iterator.hasNext())
			{
				SimpleFeature feature = iterator.next();
				
				Geometry geo_map = (Geometry)feature.getAttribute("the_geom");
				//System.out.println(feature.getAttribute("NAME_1"));
				Filter spacefilter = factory2.intersects(factory2.property(geometrylocalname), factory2.literal(geo_map));
				SimpleFeatureCollection tempcollection = new ListFeatureCollection(Utils.getPointSchema(),_list_points);
				SimpleFeatureCollection subcollection = tempcollection.subCollection(spacefilter);
				SimpleFeatureIterator iterator2 = subcollection.features();
				while(iterator2.hasNext())
				{
					SimpleFeature feature2 = iterator2.next();
					//System.out.println(feature2.getDefaultGeometry().toString());
					list_points_result.add(feature2);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Integer> indexs = new ArrayList<>();
		for (SimpleFeature tempfeature : list_points_result) {
			int index = (Integer)tempfeature.getAttribute("id");
			//还得加入到里面去
			indexs.add(index);
		}
		return indexs;
	}
	
	@Override 
	public HashSet<Object> getDistinctAttributeValues(String attributename)
	{
		HashSet<Object> list_result = new HashSet<>();
		try {
			SimpleFeatureCollection collection = source.getFeatures();
			SimpleFeatureIterator iterator = collection.features();
			while(iterator.hasNext())
			{
				SimpleFeature feature = iterator.next();
				list_result.add(feature.getAttribute(attributename));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list_result;
		
	}
	public String generateCQL(String attributename,Object value)throws Exception
	{
		if(value instanceof String)
		{
			return attributename+"='"+value+"'";
		}
		else if(value instanceof Integer)
		{
			return attributename+"="+value;
		}
		else
		{
			throw new Exception("not supported the "+value.getClass().getName()+" class");
		}
	}
	@Override
	public HashSet<String> getDistinctAttributenames()
	{
		HashSet<String> resultset = new HashSet<>();
		try {
			SimpleFeatureType type  = source.getSchema();
			List<AttributeDescriptor> descriptors = type.getAttributeDescriptors();
			for (AttributeDescriptor descriptor : descriptors) {
				String name = descriptor.getLocalName();
				resultset.add(name);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultset;
	}
	@Override
	public HashMap<Object, ArrayList<Integer>> spaceDividedbyAttribute(List<SimpleFeature> _list_points,
			String attribudename) {
		// TODO Auto-generated method stub
		HashMap<Object, ArrayList<Integer>> map = new HashMap<>();
		HashSet<Object> attributevalues = getDistinctAttributeValues(attribudename);
		try{
			for (Object value : attributevalues) {
				String cql = generateCQL(attribudename, value);
				ArrayList<Integer> templist = spaceFilterbyCQL(_list_points, cql);
				map.put(value, templist);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	

}
