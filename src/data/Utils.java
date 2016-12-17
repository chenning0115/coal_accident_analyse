package data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.SimpleContentFeatureMapEntry;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.expression.ThisPropertyAccessorFactory;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

import weka.core.Instances;

public class Utils {

	private static SimpleFeatureType schema = null;
	public static String shp_china = "resourse/shp/province_singlepart.shp";
	private static ArrayList<SimpleFeature> instancefeatures = null;
	public static SimpleFeatureType getPointSchema()
	{
		if(schema==null)
		{
			SimpleFeatureTypeBuilder typebuilder = new SimpleFeatureTypeBuilder();
			typebuilder.setName("points");
			CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
			typebuilder.setCRS(crs);
			typebuilder.add("the_geom",Point.class);
			typebuilder.add("id",Integer.class);
			
			typebuilder.setDefaultGeometry("the_geom");
			schema = typebuilder.buildFeatureType();
			return schema;
		}
		else {
			return schema;
		}
	}
	public static ArrayList<SimpleFeature> points2featuers(LRInstances lrInstances)
	{
		if(instancefeatures == null)
		{
			SimpleFeatureType type = getPointSchema();
	        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
	        WKTReader reader = new WKTReader(geometryFactory);
	        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
	        
	        ArrayList<SimpleFeature> features_class = new ArrayList<SimpleFeature>();
	        Instances data =lrInstances.instances;
	        for(int i = 0;i<data.numInstances();i++)
	        {
	        	try{
	        		Point p = (Point) reader.read("POINT("+lrInstances.GetValueofgeo_lon(i)+" "+lrInstances.GetValueofgeo_lat(i)+")");
		        	featureBuilder.add(p);
		        	featureBuilder.add(i);
		        	features_class.add(featureBuilder.buildFeature(null));
	        	}catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        }
	        instancefeatures = features_class;
		}
		return instancefeatures;
		
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
