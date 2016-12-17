package mydata;

import java.io.BufferedReader;  
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang.ObjectUtils.Null;

import mydata.DataProcess.tuple;

public class MKgeocoding {

	Baidumap baidumap = new Baidumap();
	ArrayList<DataProcess.tuple> list_tuple = new ArrayList<>();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	public void readdata(String path)
	{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"));
			String tempstr = null;
			while((tempstr=reader.readLine())!=null)
			{
				String[] strs = tempstr.split(",");
				DataProcess.tuple temptuple = new tuple(dateFormat.parse(strs[1]),Integer.parseInt(strs[2]));
				temptuple.ID = Integer.parseInt(strs[0]);
				temptuple.location = strs[4];
				temptuple.lng=temptuple.lat = 0;
				System.out.println(temptuple.location);
				list_tuple.add(temptuple);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	 
	public void readdata2(String path)
	{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"));
			String tempstr = null;
			while((tempstr=reader.readLine())!=null)
			{
				String[] strs = tempstr.split(",");
				DataProcess.tuple temptuple = new tuple(dateFormat.parse(strs[1]),Integer.parseInt(strs[2]));
				temptuple.ID = Integer.parseInt(strs[0]);
				temptuple.lat=Double.parseDouble(strs[3]);
				temptuple.lng=Double.parseDouble(strs[4]);
				temptuple.location = strs[5].trim();
				System.out.println(temptuple.location);
				list_tuple.add(0, temptuple);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void matchDataFromtypefile(String typefilepath,String savepath)
	{
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(typefilepath)), "GBK"));
			String line = null;
			while((line = reader.readLine())!=null)
			{
				String[] strs = line.split(",");
				int id = Integer.parseInt(strs[0]);
				int death = Integer.parseInt(strs[1]);
				String type = strs[2];
				for(int i=0;i<list_tuple.size();i++)
				{
					tuple temptuple = list_tuple.get(i);
					if(temptuple.ID==id&&temptuple.numdeath==death)
					{
						temptuple.type = type;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try{
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(savepath)),"GBK"));
			for(int i =0;i<list_tuple.size();i++)
			{
				tuple temp = list_tuple.get(i);
				writer.println(temp.ID+","+dateFormat2.format(temp.date)+","+temp.numdeath+","+temp.lat+","+temp.lng+","+temp.location+","+temp.type);
				writer.flush();
			}
			writer.close();
			System.out.println("success output now!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public void Getlnglatfrombaidu(String path)
	{
		for(int i =0;i<list_tuple.size();i++)
		{
			if(list_tuple.get(i).lat!=0&&list_tuple.get(i).lng!=0) continue;
			try{
				String tempstr = baidumap.querygeocoding(list_tuple.get(i).location);
				String[] strs = tempstr.split(",");
				list_tuple.get(i).lat = Double.parseDouble(strs[0]);
				list_tuple.get(i).lng = Double.parseDouble(strs[1]);
				System.out.println("success "+i+" "+tempstr);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		try{
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path)),"GBK"));
			for(int i =0;i<list_tuple.size();i++)
			{
				tuple temp = list_tuple.get(i);
				writer.println(temp.ID+","+dateFormat2.format(temp.date)+","+temp.numdeath+","+temp.lat+","+temp.lng+","+temp.location+","+temp.type);
				writer.flush();
			}
			writer.close();
			System.out.println("success output now!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MKgeocoding mKgeocoding = new MKgeocoding();
		//mKgeocoding.readdata("resourse/meikuangshigu.csv");
		mKgeocoding.readdata2("resourse/meikuangdata3.csv");
		//mKgeocoding.Getlnglatfrombaidu("resourse/meikuangdata4.csv");
		mKgeocoding.matchDataFromtypefile("resourse/meikuangshigutype.csv","resourse/meikuang5.csv");
	}

}
