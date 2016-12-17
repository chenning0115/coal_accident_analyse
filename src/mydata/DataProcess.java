package mydata;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashMap;

import javax.imageio.stream.FileImageInputStream;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.security.auth.kerberos.KerberosKey;

import org.omg.CORBA.portable.RemarshalException;
import org.w3c.dom.css.ElementCSSInlineStyle;

public class DataProcess {

	ArrayList<tuple> list = new ArrayList<>();
	HashMap<String,Integer> index_province = new HashMap<>();
	HashMap<String,Integer> index_type = new HashMap<>();
	HashMap<String,Integer> index_death = new HashMap<>();
	HashMap<String, Integer> index_month = new HashMap<>();
	ArrayList<String> sign = new ArrayList<>();
	
	public static String deathtype(int numdeath)
	{
		if(numdeath>=30) return "veryserious";
		else if(numdeath>=10) return "serious";
		else if(numdeath>=3) return"relativelyserious";
		else return "general";
	}
	
	public static class tuple
	{
		public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		public int ID;
		public java.util.Date date;
		public int numdeath;
		public String location;
		public String str_type;
		public String nptext;
		public String province;
		public String type;
		public double lng;
		public double lat;
		
		public tuple(String _date,int _numdeath,String _nptext)
		{
			try {
				date = dateFormat.parse(_date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//date = new java.util.Date(Integer.parseInt(str_Dates[0]),Integer.parseInt(str_Dates[1]),Integer.parseInt(str_Dates[2]));
			this.numdeath = _numdeath;
			this.nptext = _nptext;
			this.location=_nptext;
			this.str_type=_nptext;
		}
		public tuple(java.util.Date _date,int _numdeath) {
			// TODO Auto-generated constructor stub
			this.date = _date;
			this.numdeath = _numdeath;
		}
		@Override
		public String toString()
		{
			
			return ID+","+dateFormat.format(date)+","+numdeath+","+type+","+location+","+str_type+","+nptext;
		}
		
	}
	
	public void processdata(String filepath)
	{
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(filepath)),"GBK"));
			for(int i =0;i<list.size();i++)
			{
				tuple temptuple = list.get(i);
		   		
				int index1 = temptuple.nptext.indexOf(",");
				if(index1<0) index1=temptuple.nptext.indexOf("，");
				int index2 = temptuple.nptext.indexOf("煤矿");
				if(index1>=index2) index1=0;
				if(index1>=0&&index2>=0) temptuple.location = temptuple.nptext.substring(index1+1,index2+2);
				int index3 = temptuple.nptext.indexOf("发生");
				int index4 = temptuple.nptext.indexOf("事故");
				if(index4<=0) index4=temptuple.nptext.indexOf("，",index3);
				if(index3>=0&&temptuple.nptext.contains("发生一起")) index3+=2;
				//System.out.println("index1="+index1+"index2="+index2+"index3="+index3+"index4="+index4+": "+temptuple.nptext);
				if(index3>=index4) index3=0;
				if(index3>=0&&index4>=0&&index4>(index3+2))temptuple.str_type = temptuple.nptext.substring(index3+2,index4);
			}
			//GetProvince("resourse/chinaprovince");
			GetType("resourse/typemeikuangshigu");
			for(int i=0;i<list.size();i++)
			{
				tuple temp = list.get(i);
				writer.println(temp.toString());
				System.out.println(temp.toString());
				writer.flush();
			}
			writer.close();
			//GenerateTable("resourse/table.csv");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void GenerateTable(String path)
	{
		index_death.put("veryserious",sign.size());sign.add("veryserious");
		index_death.put("serious",sign.size());sign.add("serious");
		index_death.put("relativelyserious",sign.size());sign.add("relativelyserious");
		index_death.put("general",sign.size());sign.add("general");
		for(int i = 1;i<=12;i++)
		{
			index_month.put(""+i, sign.size());
			sign.add(""+i);
		}
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path))));
			for(int i =0;i<sign.size();i++)
				writer.print(sign.get(i)+",");
			writer.println("");
			boolean[][] table = new boolean[list.size()][sign.size()];
			for(int i =0;i<list.size();i++)
			{
				for(int j = 0;j<sign.size();j++)
				{
					table[i][j] = false;
				}
			}
			for(int i =0;i<list.size();i++)
			{
				tuple temp = list.get(i);
				table[i][index_death.get(deathtype(temp.numdeath))]=true;
				table[i][index_month.get(""+(temp.date.getMonth()+1))]=true;
				if(index_province.containsKey(temp.province)) table[i][index_province.get(temp.province)] = true;
				if(index_type.containsKey(temp.type)) table[i][index_type.get(temp.type)]=true;
			}
			for(int i =0;i<list.size();i++)
			{
				for(int j = 0;j<sign.size();j++)
				{
					if(table[i][j]) writer.print("t,");
					else writer.print(" ,");
				}
				writer.println();
			}
			writer.flush();
			writer.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public void GetProvince(String profilepath)
	{
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(profilepath))));
			HashMap<String, String> types = new HashMap<>();
			String tempstr = null;
			while((tempstr = reader.readLine())!=null)
			{
				//System.out.println(tempstr);
				String[] kv = tempstr.split(" ");
				types.put(kv[0], kv[1]);
				if(!index_province.containsKey(kv[1]))
				{
					index_province.put(kv[1],sign.size());
					sign.add(kv[1]);
				}
				
			}
			Set<String> kes = types.keySet();
			for(int i = 0;i<list.size();i++)
			{
				tuple temptuple = list.get(i);
				boolean check = false;
				for (String key : kes) {
					if(temptuple.location.contains(key))
					{
						temptuple.province = types.get(key);
						check = true;
						break;
					}
				}
				if(!check)
				{
					for (String key : kes) {
						if(temptuple.nptext.contains(key))
						{
							
							temptuple.province = types.get(key);
							check = true;
							break;
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void GetType(String profilepath)
	{
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(profilepath))));
			HashMap<String, String> types = new HashMap<>();
			String tempstr = null;
			while((tempstr = reader.readLine())!=null)
			{
				String[] kv = tempstr.split(" ");
				types.put(kv[0], kv[1]);
				if(!index_type.containsKey(kv[1]))
				{
					index_type.put(kv[1], sign.size());
					sign.add(kv[1]);
				}
			}
			
			Set<String> kes = types.keySet();
			for(int i = 0;i<list.size();i++)
			{
				tuple temptuple = list.get(i);
				boolean check = false;
				for (String key : kes) {
					if(temptuple.str_type.contains(key))
					{
						
						temptuple.type = types.get(key);
						check = true;
						break;
					}
				}
				if(!check)
				{
					for (String key : kes) {
						if(temptuple.nptext.contains(key))
						{
							
							temptuple.type = types.get(key);
							check = true;
							break;
						}
					}
				}
				if(temptuple.type==null) temptuple.type = "qita";
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readdata(String path)
	{
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"));
			String tempstr = null;
			int i = 0;
			while((tempstr=reader.readLine())!=null)
			{
				String[] strs = tempstr.split("#");
				//System.out.println(strs[2]);
				tuple temptuple = new tuple(strs[0], Integer.parseInt(strs[1]),strs[2]);
				temptuple.ID = i;i++;
				list.add(temptuple);
			}
			System.out.println("num="+list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DataProcess process = new DataProcess();
		process.readdata("resourse/meikuangshigu.txt");
		process.processdata("resourse/meikuangshigutype.csv");
	}

}
