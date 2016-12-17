package mydata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;


public class Baidumap {

	private String baseurl = "http://api.map.baidu.com/geocoder/v2/";
	private String baiduapikey = "D1d360f47c97bf296e6fbae43d1eb311";
	private String geturl(Map<String, String> param)
	{
		String url = baseurl+"?ak="+baiduapikey;
		for (String key : param.keySet()) {
			String value = param.get(key);
			url=url+"&"+key+"="+value;
		}
		return url;
	}
	/*
	 * 返回lat,lon
	 */
	public String querygeocoding(String place)
	{
		CloseableHttpResponse response=null;
		try{
		HashMap<String, String> map = new HashMap<>();
		//map.put("callback","renderOption");
		map.put("address",place);
		map.put("output","json");

		String url = geturl(map);
		//System.out.println(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpGet httpget = new HttpGet(url);
		System.out.println(url);
		response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream ins = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins,"UTF-8"));
		String line = null;
		StringBuilder builder = new StringBuilder();
		while((line=reader.readLine())!=null)
		{
			builder.append(line);
		}
		//System.out.println(builder.toString());
		response.close();
		String str_result = builder.toString();
		JSONObject jObject = new JSONObject(str_result);
		JSONObject jObject2 = jObject.getJSONObject("result").getJSONObject("location");
		double lon = jObject2.getDouble("lng");
		double lat = jObject2.getDouble("lat");
		return ""+lat+","+lon;
		
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Baidumap baidumap = new Baidumap();
		System.out.println(baidumap.querygeocoding("云南省曲靖市师宗县私庄煤矿"));
	}

}
