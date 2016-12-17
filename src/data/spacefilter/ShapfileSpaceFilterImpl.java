package data.spacefilter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import data.Utils;

public class ShapfileSpaceFilterImpl extends AbstractSpaceFilter{

	private static ShapfileSpaceFilterImpl singleton;
	
	public static ShapfileSpaceFilterImpl GetInstanceofShapfileSpaceFilterImple(String shppath)
	{
		if((singleton==null)||(!singleton.shppath.equals(shppath)))
		{
			singleton = new ShapfileSpaceFilterImpl(shppath);
			singleton.setFeatureSource();
		}
		return singleton;
	}
	
	private String shppath;
	private ShapfileSpaceFilterImpl(String shapefilepath)
	{
		this.shppath = shapefilepath;
	}

	private SimpleFeatureSource readshapefile(String path)
	{
		 SimpleFeatureSource source = null;
		try{
			 File file = new File(path);
			 Map<String, Object> map = new HashMap<String, Object>();
			 map.put("url", file.toURI().toURL());
			 DataStore dataStore = DataStoreFinder.getDataStore(map);
			 String typeName = dataStore.getTypeNames()[0];
			 source = dataStore.getFeatureSource(typeName);
			 System.out.println("success in reading shpfile!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return source;
	}

	@Override
	public void setFeatureSource() {
		// TODO Auto-generated method stub
		System.out.println(this.shppath);
		SimpleFeatureSource tempsource = readshapefile(this.shppath);
		this.source = tempsource;
	}

}
