package data;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import opendap.dap.test.expr_test;
import statistics.timeserise.TimeSeriseStatistics;
import weka.associations.Apriori;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;

/*
 * 锟斤拷锟斤拷锟斤拷weka锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷instance之锟斤拷锟角癸拷锟斤拷锟斤拷系锟斤拷锟斤拷锟斤拷锟角凤拷锟斤拷锟斤拷系锟斤拷锟斤拷为锟斤拷instance锟斤拷锟角接口伙拷锟斤拷锟斤拷锟斤拷
 * 锟斤拷锟角撅拷锟斤拷锟洁，锟斤拷锟斤拷锟斤拷weka锟斤拷应锟矫革拷锟接ｏ拷一锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷LRinstance锟斤拷锟皆碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟叫达拷锟捷ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息
 * 锟酵会被抹去锟斤拷也锟斤拷失去锟剿讹拷态锟斤拷锟斤拷锟皆ｏ拷要锟斤拷锟斤拷应锟斤拷weka锟叫的革拷锟街癸拷锟杰ｏ拷锟斤拷锟斤拷要锟斤拷锟斤拷Instance
 * 锟叫的凤拷锟斤拷锟斤拷锟斤拷锟角非筹拷锟斤拷锟接的★拷锟斤拷锟斤拷态锟斤拷实锟斤拷锟截碉拷锟斤拷锟斤拷锟斤拷锟洁都去锟斤拷写锟斤拷同锟侥猴拷锟斤拷锟斤拷然锟斤拷锟斤拷锟斤拷涌诒锟教ｏ拷锟截碉拷锟斤拷锟斤拷
 * 锟斤拷写锟斤拷锟叫凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷系锟斤拷锟斤拷员冉习锟饺拷锟斤拷锟接凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷API锟斤拷为一锟斤拷锟节猴拷锟接ｏ拷锟斤拷么锟教筹拷API锟斤拷锟斤拷锟斤拷
 * 锟斤拷锟斤拷锟斤拷锟铰癸拷锟杰诧拷锟斤拷一锟斤拷锟矫办法锟斤拷锟斤拷为锟斤拷锟皆伙拷锟斤拷息锟结被锟节黑猴拷锟斤拷锟斤拷抹去锟斤拷锟斤拷锟角斤拷锟斤拷强锟斤拷转锟斤拷锟斤拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟斤拷系锟斤拷锟斤拷锟剿碉拷前锟斤拷锟斤拷猓�
 * 锟斤拷锟斤拷之锟解，锟斤拷锟斤拷要锟斤拷锟角癸拷锟斤拷锟斤拷系锟缴匡拷锟揭帮拷全
 */
public class LRInstances {

	public Instances instances = null;//锟斤拷装锟斤拷weka锟斤拷锟斤拷锟斤拷锟捷结构 Instances锟斤拷锟斤拷示一锟斤拷锟斤拷锟斤拷锟斤拷relation
	private int sign_attr_geolat = -1;//锟斤拷志锟斤拷锟斤拷锟斤拷维锟斤拷锟斤拷锟皆碉拷位锟斤拷
	private int sign_attr_geolon = -1;
	private int sign_attr_date = -1;//锟斤拷志锟斤拷锟斤拷锟斤拷时锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟叫达拷锟斤拷时锟斤拷锟斤拷锟皆碉拷位锟斤拷
	

	public LRInstances(Instances _instances)
	{
		this.instances = _instances;
	}
	public LRInstances(Instances _Instances, int _sign_attr_geolat, int _sign_attr_geolon,int _sign_attr_date)
	{
		this.instances = _Instances;
		this.sign_attr_geolat=_sign_attr_geolat;
		this.sign_attr_geolon=_sign_attr_geolon;
		this.sign_attr_date=_sign_attr_date;
	}
	
	public void setsign_attr_geolat(int _sign_attr_geolat)
	{
		this.sign_attr_geolat = _sign_attr_geolat;
	}
	public int getsign_attr_geolat()
	{
		return this.sign_attr_geolat;
	}
	public int getsign_attr_lng()
	{
		return this.sign_attr_geolon;
	}
	public int getsign_attr_date()
	{
		return this.sign_attr_date;
	}
	public void setsign_attr_geolon(int _sign_attr_geolon)
	{
		this.sign_attr_geolon = _sign_attr_geolon;
	}
	public void setsign_attr_date(int _sign_attr_date)
	{
		this.sign_attr_date = _sign_attr_date;
	}
	/*
	 * read data from database
	 */
	public static LRInstances loadfromDB(String url,String usrname,String passwd,String query)throws Exception
	{
		 DatabaseLoader loader = new DatabaseLoader();
		 loader.setSource(url,usrname,passwd);
		 loader.setQuery(query);
		 Instances data = loader.getDataSet();
		 LRInstances lrInstances = new LRInstances(data);
		 System.out.println("sucessfully load from DB length = "+data.numInstances()+" attrlength = "+data.numAttributes());
		 for(int i =0;i<data.numAttributes();i++)
		 {
			 System.out.println(data.attribute(i).name()+" "+data.attribute(i).typeToString(data.attribute(i)));
		 }
		 return lrInstances;
	}
	public void save2DBBatch(String jdbcurl,String user,String passwd,String tablename)
	{
		try{
			DatabaseSaver saver = new DatabaseSaver();
		    saver.setDestination(jdbcurl, user,passwd);
		    // we explicitly specify the table name here:
		    saver.setTableName(tablename);
		    saver.setRelationForTableName(false);
		    // or we could just update the name of the dataset:
		    // saver.setRelationForTableName(true);
		    // data.setRelationName("whatsoever2");
		    saver.setInstances(instances);
		    saver.writeBatch();
		    System.out.println("successfully output to database of "+tablename);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public static LRInstances loadfromfile(String filepath) throws Exception
	{
		Instances data = DataSource.read(filepath);
		LRInstances lrInstances = new LRInstances(data);
		System.out.println("sucessfully load from "+filepath+" length = "+data.numInstances()+" attrlength = "+data.numAttributes());
		 for(int i =0;i<data.numAttributes();i++)
		 {
			 System.out.println(data.attribute(i).name()+" "+data.attribute(i).typeToString(data.attribute(i)));
		 }
		 return lrInstances;
	}
	/*
	 * get the value of geo_lat
	 */
	public double GetValueofgeo_lat(int indexofinstance)throws Exception
	{
		if(this.sign_attr_geolat==-1) throw new Exception("no sign of geo");
		Instance ins = this.instances.get(indexofinstance);
		return ins.value(this.sign_attr_geolat);
	}
	/*
	 * get the value of geo_lon
	 */
	public double GetValueofgeo_lon(int indexofinstance) throws Exception
	{
		if(this.sign_attr_geolon==-1) throw new Exception("no sign of geo");
		Instance ins = this.instances.get(indexofinstance);
		return ins.value(this.sign_attr_geolon);
	}
	/*
	 * get the date always used to process time-series data
	 */
	public Date GetValueofDate(int indexofinstance) throws Exception
	{
		if(this.sign_attr_date==-1) throw new Exception("no sign of date");
		Instance ins = this.instances.get(indexofinstance);
		Attribute attr = ins.attribute(this.sign_attr_date);
		if(!attr.isDate()) throw new Exception("not the date attribute");
		//System.out.println(attr.getDateFormat());
		SimpleDateFormat sDateFormat = new SimpleDateFormat(attr.getDateFormat());
		return sDateFormat.parse(attr.formatDate(ins.value(this.sign_attr_date)));
	}
	
	public double getValue(int indexofinstance,int indexofattribute)
	{
		Instance ins = this.instances.get(indexofinstance);
		return ins.value(indexofattribute);
	}
	public String getStringValue(int indexofinstance,int indexofattribute)
	{
		Instance ins = this.instances.get(indexofinstance);
		Attribute attribute = ins.attribute(indexofattribute);
		if(attribute.isDate()||attribute.isNominal()||attribute.isString())
		return ins.stringValue(indexofattribute);
		return "";
	}
	//随意给一个值以及该值所在的列，返回该值实际存储的double值
	public double ConvertValue2Double(int indexofAttribute,Object obj)throws Exception
	{
		//System.out.println("obj="+obj);
		Attribute attribute = this.instances.attribute(indexofAttribute);
		switch (attribute.type()) {
		case Attribute.DATE:
			throw new Exception("not supported");
		case Attribute.NUMERIC:
			return (double)obj;
		case Attribute.NOMINAL:
			return attribute.indexOfValue(obj.toString());
		case Attribute.STRING:
			return attribute.indexOfValue(obj.toString());
		default:
			throw new Exception("not supported");
		
		}
	}
	
	public String getDescription(int indexofinstance)
	{
		return this.instances.get(indexofinstance).toString();
	}
	public int getSize()
	{
		return instances.size();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			LRInstances lrInstances = LRInstances.loadfromfile("resourse/table3.csv");
			 Instances data = lrInstances.instances; 
			   // data.setClassIndex(data.numAttributes() - 1);

			    // build associator
			    Apriori apriori = new Apriori();
			    //apriori.setClassIndex(data.classIndex());
			    apriori.buildAssociations(data);

			    // output associator
			    System.out.println(apriori);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
