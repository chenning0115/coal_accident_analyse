package mydata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import opendap.dap.test.expr_test;

public class ToDataBase {

	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	private static BasicDataSource dataSource = null;
	public static synchronized Connection GetPostgresqlConnection()throws  SQLException { 
        if (dataSource == null) { 
            init(); 
        } 
        Connection conn = null; 
        if (dataSource != null) { 
            conn =dataSource.getConnection(); 
        } 
        return conn; 
    } 
	public static void init() {

	       if (dataSource != null) { 
	            try { 
	               dataSource.close(); 
	            } catch(Exception e) { 
	               // 
	            } 
	            dataSource =null; 
	        }

	       try { 
	           Properties p= new Properties(); 
	           p.setProperty("driverClassName","org.postgresql.Driver"); 
	           p.setProperty("url","jdbc:postgresql://localhost:5432/postgres"); 
	           p.setProperty("password", "admin"); 
	           p.setProperty("username", "postgres"); 
	           p.setProperty("maxActive", "500"); 
	           p.setProperty("maxIdle", "490"); 
	           p.setProperty("maxWait", "500"); 
	           p.setProperty("removeAbandoned", "false"); 
	           p.setProperty("removeAbandonedTimeout", "120"); 
	           p.setProperty("testOnBorrow", "true"); 
	           p.setProperty("logAbandoned", "true");

	           dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);

	       } catch (Exception e) { 
	            // 
	        } 
	    }
	public static void createTable()
	{
		String sql = "CREATE TABLE coalmineaccidents ("
				  +"id int NOT NULL,"
				  +"lng float,"
				  +"lat float,"
				  +"date timestamp,"
				  +"death int,"
				  +"location VARCHAR(45),"
				  +"type VARCHAR(20),"
				  +"PRIMARY KEY (id));";
		try{
			Connection con = GetPostgresqlConnection();
			Statement statement = con.createStatement();
			statement.execute(sql);
			statement.close();
			con.close();
			System.out.println("create table successfully!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void csv2db(String csvpath)
	{
		try{
			Connection con = GetPostgresqlConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(csvpath)),"GBK"));
			String line = null;
			int i = 0;
			while((line=reader.readLine())!=null)
			{
				String[] strs = line.split(",");
				String sql = "insert into coalmineaccidents(id,lng,lat,date,death,location,type) values("
						+ strs[0]+","
						+ strs[4]+","
						+ strs[3]+","
						+ "'"+dateFormat.format(dateFormat2.parse(strs[1]))+"',"
								+ strs[2]+","
								+ "'"+strs[5]+"',"
						 + "'"+strs[6]+"');";
				Statement statement = con.createStatement();
				System.out.println(sql);
				statement.execute(sql);
				statement.close();
				System.out.println("success "+ i);
				i++;
			}
			reader.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		createTable();
		csv2db("resourse/meikuang5.csv");
	}

}
