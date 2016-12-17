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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.postgresql.translation.messages_bg;

import mydata.DataProcess.tuple;
import opendap.dap.test.expr_test;

public class MeikuangTabelGenerator {

	ArrayList<DataProcess.tuple> list_tuple = new ArrayList<>();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	HashMap<String,Integer> index_province = new HashMap<>();
	HashMap<String, String> types = new HashMap<>();
	HashMap<String,Integer> index_type = new HashMap<>();
	HashMap<String,Integer> index_death = new HashMap<>();
	HashMap<String, Integer> index_month = new HashMap<>();
	HashMap<String, Integer> index_quarter = new HashMap<>();
	ArrayList<String> sign = new ArrayList<>();
	
	public static String deathtype(int numdeath)
	{
		if(numdeath>=30) return "veryserious";
		else if(numdeath>=10) return "serious";
		else if(numdeath>=3) return"relativelyserious";
		else return "general";
	}
	
	 public static int getSeason(Date date) {  
		  
	        int season = 0;  
	  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(date);  
	        int month = c.get(Calendar.MONTH);  
	        switch (month) {  
	        case Calendar.JANUARY:  
	        case Calendar.FEBRUARY:  
	        case Calendar.MARCH:  
	            season = 1;  
	            break;  
	        case Calendar.APRIL:  
	        case Calendar.MAY:  
	        case Calendar.JUNE:  
	            season = 2;  
	            break;  
	        case Calendar.JULY:  
	        case Calendar.AUGUST:  
	        case Calendar.SEPTEMBER:  
	            season = 3;  
	            break;  
	        case Calendar.OCTOBER:  
	        case Calendar.NOVEMBER:  
	        case Calendar.DECEMBER:  
	            season = 4;  
	            break;  
	        default:  
	            break;  
	        }  
	        return season;  
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
				DataProcess.tuple temptuple = new tuple(dateFormat2.parse(strs[1]),Integer.parseInt(strs[2]));
				temptuple.ID = Integer.parseInt(strs[0]);
				temptuple.lat=Double.parseDouble(strs[3]);
				temptuple.lng=Double.parseDouble(strs[4]);
				temptuple.location = strs[5].trim();
				temptuple.type = strs[6];
				//System.out.println(temptuple.location);
				list_tuple.add(0, temptuple);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	} 
	
	public void GetProvinceandetypesign(String profilepath)
	{
		for(int i=1;i<=4;i++)
		{
			index_quarter.put("quarter_"+i, sign.size());sign.add("quarter_"+i);
		}
		for(int i = 1;i<=12;i++)
		{
			index_month.put("month_"+i, sign.size());
			sign.add("month_"+i);
		}
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(profilepath))));
			
			String tempstr = null;
			while((tempstr = reader.readLine())!=null)
			{
				//System.out.println(tempstr);
				String[] kv = tempstr.split(" ");
				types.put(kv[0], kv[1]);
				if(!index_province.containsKey(kv[0]))
				{
					index_province.put(kv[0],sign.size());
					sign.add(kv[1]);
				}
				
			}
			Set<String> kes = types.keySet();
			for(int i = 0;i<list_tuple.size();i++)
			{
				tuple temptuple = list_tuple.get(i);
				boolean check = false;
				for (String key : kes) {
					if(temptuple.location.contains(key))
					{
						temptuple.province = key;
						check = true;
						break;
					}
				}
				if(!check)
				{
					temptuple.province="无";
				}
				
			}
			
			//typesign
			index_type.put("dingban", sign.size());sign.add("dingban");
			index_type.put("yunshu", sign.size());sign.add("yunshu");
			index_type.put("shuizai", sign.size());sign.add("shuizai");
			index_type.put("huozai", sign.size());sign.add("huozai");
			index_type.put("wasi", sign.size());sign.add("wasi");
			index_type.put("fangpao", sign.size());sign.add("fangpao");
			index_type.put("jidian", sign.size());sign.add("jidian");
			index_type.put("qita", sign.size());sign.add("qita");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void printdata(String path)
	{
		readdata2("resourse/meikuang5.csv");
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path)),"GBK"));
			GetProvinceandetypesign("resourse/chinaprovince");
			writer.println("id,province,death,season,month,type");
			for(int i=0;i<list_tuple.size();i++)
			{
				tuple temp = list_tuple.get(i);
				writer.println(temp.ID+","+types.get(temp.province)+","+deathtype(temp.numdeath)+","+"quarter_"+getSeason(temp.date)+","+("month_"+(temp.date.getMonth()+1))+","+temp.type);
				writer.flush();
			}
			writer.close();
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
		
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path)),"GBK"));
			writer.print("id"+",");
			for(int i =0;i<sign.size();i++)
				{
					writer.print(sign.get(i)+",");
					System.out.print(sign.get(i)+",");
				}
			writer.println("");
			writer.flush();
			boolean[][] table = new boolean[list_tuple.size()][sign.size()];
			for(int i =0;i<list_tuple.size();i++)
			{
				for(int j = 0;j<sign.size();j++)
				{
					table[i][j] = false;
				}
			}
			for(int i =0;i<list_tuple.size();i++)
			{
				tuple temp = list_tuple.get(i);
				table[i][index_quarter.get("quarter_"+getSeason(temp.date))]=true;
				table[i][index_death.get(deathtype(temp.numdeath))]=true;
				table[i][index_month.get("month_"+(temp.date.getMonth()+1))]=true;
				if(index_province.containsKey(temp.province)) table[i][index_province.get(temp.province)] = true;
				if(index_type.containsKey(temp.type)) table[i][index_type.get(temp.type)]=true;
			}
			for(int i =0;i<list_tuple.size();i++)
			{
				writer.print(list_tuple.get(i).ID+",");
				for(int j = 0;j<sign.size();j++)
				{
					if(table[i][j]) writer.print("t,");
					else writer.print(" ,");
				}
				writer.println();
				writer.flush();
			}
			writer.flush();
			writer.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	} 
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MeikuangTabelGenerator generator = new MeikuangTabelGenerator();
		//generator.readdata2("resourse/meikuang5.csv");
		//generator.GetProvinceandetypesign("resourse/chinaprovince");
		//generator.GenerateTable("resourse/table2.csv");
		generator.printdata("resourse/test.csv");
	}

}
