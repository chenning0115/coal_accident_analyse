package analyse.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import data.LRInstances;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.gui.Main;

public class FastSearchBasedGeo implements ILRCluster {

	LRInstances lrinstances = null;
	Instances data =null;
	double r_square;
	private int minptsasnoise = 5;
	private double disinterval = 1e-15;
	int numclass = 0;
	
	int maxpts = Integer.MIN_VALUE;
	int minpts = Integer.MAX_VALUE;
	double maxdis =Double.MIN_VALUE;
	double mindis = Double.MAX_VALUE;
	
	List<Node> list;
	
	public FastSearchBasedGeo(LRInstances _lLrInstances,double _r_square) {
		// TODO Auto-generated constructor stub
		this.lrinstances = _lLrInstances;
		this.data = lrinstances.instances;
		this.r_square = _r_square;
	}
	
	public double caldistancesquare(int index_a,int index_b)throws Exception
	{
		double delt_lng = lrinstances.GetValueofgeo_lon(index_a)-lrinstances.GetValueofgeo_lon(index_b);
		double delt_lat = lrinstances.GetValueofgeo_lat(index_a)-lrinstances.GetValueofgeo_lat(index_b);
		return delt_lat*delt_lat+delt_lng*delt_lng;
	}
	
	public int getNumPointWithin(int index)throws Exception
	{
		int count = 0;
		for(int i=0;i<data.numInstances();i++)
		{
			
			if(caldistancesquare(index, i)<=r_square)
				count++;
		}
		return count;
	}
	
	static class Node
	{
		public int pointindex;
		public Instance instance;
		public int density_num;
		public double distance;
		public double tempval;
		public boolean checkinter = false;
		public int sign = 0;//0 represents not being classified , 1 represents has been classified, 2 represents center point,-1 represents noise point.
		public int classtype = -1; //-1 represents no class; -2 represents noise point
		public Node mindisnode = null;//note the mindistance and large density point reference
	}
	
	public class calculatedensityclass implements Runnable
	{
		private int i =0;
		public calculatedensityclass(int _i) {
			// TODO Auto-generated constructor stub
			this.i = _i;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				System.out.println("start calculate density of point "+i);
				Node curnode = new Node();
				curnode.instance = data.get(i);
				curnode.density_num = getNumPointWithin(i);
				curnode.pointindex = i;
				list.add(curnode);
				System.out.println("finish calculate density of point "+i);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	private double getmaxmindis(List<Node> sortedlist,int index) 
	{
		Node curnode = sortedlist.get(index);
		double min = Double.MAX_VALUE;
		int minindex = -1;
		for(int i=0;i<index;i++)
		{
			Node tempnode = sortedlist.get(i);
			double dis;
			try {
				dis = caldistancesquare(curnode.pointindex,tempnode.pointindex);
				if(dis<min) {min = dis;minindex = i;}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		curnode.distance = min;
		curnode.mindisnode = sortedlist.get(minindex);
		return min;
	}
	public boolean outputmatrixdata(String path) 
	{
		try{
			FileOutputStream fStream = new FileOutputStream(new File(path));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(fStream));
			maxpts = Integer.MIN_VALUE;
			minpts = Integer.MAX_VALUE;
			maxdis =Double.MIN_VALUE;
			mindis = Double.MAX_VALUE;
			
			for(int i = 0;i<list.size();i++)
			{
				Node tempnode = list.get(i);
				if(tempnode.density_num>maxpts) maxpts = tempnode.density_num;
				if(tempnode.density_num<minpts) minpts = tempnode.density_num;
				if(tempnode.distance>maxdis) maxdis = tempnode.distance;
				if(tempnode.distance<mindis) mindis = tempnode.distance;
			}
			for(int i = 0;i<list.size();i++)
			{
				Node tempnode = list.get(i);
				double a1 = ((double)tempnode.density_num-minpts)/(maxpts-minpts)*100;
				double a2 = (tempnode.distance-mindis)/(maxdis-mindis)*100;
				writer.println(tempnode.density_num+","+tempnode.distance+","+a1+","+a2+","+a1*a2
				+","+(1.0d/(1.0d+Math.exp(-1*a1*a2))));
			}
			writer.flush();
			writer.close();
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public java.util.List<Node> calculatepeak(String internaldatastoragepath)
	{
		list = Collections.synchronizedList(new ArrayList<Node>());
		ExecutorService threadpool = Executors.newFixedThreadPool(100);
		for(int i = 0;i<data.numInstances();i++)
		{
			Thread thread  = new Thread(new calculatedensityclass(i));
			threadpool.execute(thread);
		}
		try{
			threadpool.shutdown();
			threadpool.awaitTermination(10, TimeUnit.MINUTES);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("calculate density successfully..start to sort by density");
		
		Collections.sort(list,new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				//System.out.println(o1+" "+o2);
				if (o1.density_num > o2.density_num) return -1;
				if(o1.density_num < o2.density_num) return 1;
				return 0;
			}
		});
		System.out.println("sort by density successfully..");
		//calculate the min distance with the node which has the large density then it
		double tempmax = Double.MIN_VALUE;
		int notemaxindex = 0;
		for(int i = 1;i<list.size();i++)
		{
			double tempdis = getmaxmindis(list, i);
			if(tempdis>tempmax) 
			{
				tempmax = tempdis;
				notemaxindex = i;
			}
		}
		list.get(0).distance = tempmax;
		list.get(0).mindisnode = list.get(notemaxindex);
		System.out.println("calculate distance successfully..");
		outputmatrixdata(internaldatastoragepath);
		System.out.println("output matrix(pointswithin_r,distance) successfully..");
		double readin =0;
		try{
			System.out.println("please input times:");
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
			readin = Double.parseDouble(reader.readLine());
			System.out.println("please input the minptsnoise:");
			minptsasnoise = Integer.parseInt(reader.readLine());
			
		}catch(Exception e){e.printStackTrace();}
		numclass = 0;
		double minmaxdis = maxdis - mindis;
		int minmaxpts = maxpts - minpts;
		for(int i = 0;i<list.size();i++)
		{
			Node curnode2 = list.get(i);
			//find the center point
			if(curnode2.density_num<minptsasnoise){//find the noise point
				curnode2.sign=-1;
				curnode2.classtype=-2;
			}
			else if((((curnode2.distance-mindis)/minmaxdis*100)*(((double)curnode2.density_num-minpts)/minmaxpts*100))>=readin)
				{
					curnode2.sign=2;
					curnode2.classtype=numclass;
					numclass++;
				}
			
		}
		System.out.println("类中心寻找结束");
		
		for(int i = 0;i<list.size();i++)
		{
			Node node3 = list.get(i);
			if(node3.sign==0) findclasstype(node3);
			System.out.println("successfully classify "+i);
		}
		return list;
	}
	private int findclasstype(Node node)
	{
		if(node.mindisnode.sign>0) {node.sign=1;node.classtype=node.mindisnode.classtype;return node.classtype;}
		int classtype = findclasstype(node.mindisnode);
		node.sign = 1;
		node.classtype = classtype;
		return classtype;
		
	}
	 
	
	@Override
	public LRInstances runLRCluster(String classAttributename) {
		// TODO Auto-generated method stub
		try{
			List<Node> resultlist = calculatepeak("resourse/fastsearch.csv");
			Instances newinstances = new Instances(data, 0);
			for(int i=0;i<data.numInstances();i++)
			{
				Instance temp = (Instance)data.get(i).copy();
				newinstances.add(temp);
			}
			Add filter = new Add();
			
		    filter.setAttributeIndex("last");
		    filter.setAttributeName(classAttributename);
		    filter.setInputFormat(newinstances);
		    newinstances = Filter.useFilter(newinstances, filter);
		    for(int i=0;i<resultlist.size();i++)
		    {
		    	Node tempnode = resultlist.get(i);
		    	Instance ins = newinstances.get(tempnode.pointindex);
		    	ins.setValue(ins.numAttributes()-1,tempnode.classtype);
		    }
		    
			LRInstances resultlrins = new LRInstances(newinstances);
			resultlrins.setsign_attr_date(this.lrinstances.getsign_attr_date());
			resultlrins.setsign_attr_geolat(this.lrinstances.getsign_attr_geolat());
			resultlrins.setsign_attr_geolon(this.lrinstances.getsign_attr_lng());
			return resultlrins;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args)
	{
		try{   
			LRInstances lrInstances = LRInstances.loadfromDB("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin", "select * from coalmineaccidents");
			lrInstances.setsign_attr_date(3);
			lrInstances.setsign_attr_geolon(1);
			lrInstances.setsign_attr_geolat(2);
			FastSearchBasedGeo fastSearchBasedGeo = new FastSearchBasedGeo(lrInstances,0.0001);
			LRInstances lrInstances2 = fastSearchBasedGeo.runLRCluster("classify");
			//lrInstances2.save2DBBatch("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin","coalmineclassify");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
