package statistics.timeserise;

import java.security.spec.ECPrivateKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.function.LongToDoubleFunction;

import org.omg.CORBA.PUBLIC_MEMBER;

import data.DateFilter;
import data.LRInstances;
import statistics.DefaultLRAggregation;
import statistics.LRIAggregation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.pmml.jaxbbindings.VectorDictionary;

/*
 * ���ڶ�ʱ���������ݽ�����ͳ��
 * ��Ҫ����ͳ�Ƹ���ʱ�������ꡢ���ȡ��£���instance��������ͬʱ���Զ�instance���Խ��м򵥵ľۺϣ���SQL��
 * �ۺϵĺ���һ�£�
 * 
 * �����ڵ���ʱ�����캯����Ҫ����һ��LRInstance����֮����������ա��족���ж�����instance���з��飬ÿһ��Ϊͬһ�������
 * ������Ʋ�ͬ�ľۺϺ����Ӷ�����ʱ�������ϵ�ͳ�Ʒ���
 */
public class TimeSeriseStatistics {

	private LRInstances lrInstances;
	private Date startdate;
	private Date enddate;
	private ArrayList<ArrayList<Integer>> list_dayindexs;
	private LRIAggregation aggregationfun;
	public static int LRDate = Calendar.DATE;
	public static int LRMonth = Calendar.MONTH;
	public static int LRQuater = 993682;
	public static int LRYear = Calendar.YEAR;
	
	
	public TimeSeriseStatistics(LRInstances _lLrInstances)throws Exception
	{
		this.lrInstances = _lLrInstances;
		SetStartandEnddate();
		daystatistic();
		this.aggregationfun = new DefaultLRAggregation();
	}
	
	private void SetStartandEnddate()throws Exception
	{
		Instances data = lrInstances.instances;
		Date _startdate,_enddate;
		_startdate = _enddate = lrInstances.GetValueofDate(0);
		for(int i = 1;i<data.numInstances();i++)
		{
			Date tempdate = lrInstances.GetValueofDate(i);
			if(tempdate.before(_startdate)) _startdate = tempdate;
			if(tempdate.after(_enddate)) _enddate = tempdate;
		}
		this.startdate = new Date(_startdate.getYear(), _startdate.getMonth(), _startdate.getDate());
		this.enddate = new Date(_enddate.getYear(), _enddate.getMonth(), _enddate.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.enddate);
		calendar.add(Calendar.DATE, 1);
		this.enddate = calendar.getTime();
	}
	public void SetLRIAggregation(LRIAggregation _aAggregation)
	{
		this.aggregationfun = _aAggregation;
		//?reset();
	}
	
	/*
	 * ��instance���б�������������л��֣���¼ÿһ���instance��list��
	 */
	private void daystatistic()
	{
		long lon_stardate = startdate.getTime();
		long lon_enddate = enddate.getTime();
		int daynum = (int)((lon_enddate-lon_stardate)/1000/3600/24);
		list_dayindexs = new ArrayList<>();
		for(int i = 0;i<daynum;i++) list_dayindexs.add(new ArrayList<>());
		Instances data = lrInstances.instances;
		for(int i = 0;i<data.numInstances();i++)
		{
			try {
				Date tempdate = lrInstances.GetValueofDate(i);
				int tempindex = (int)((tempdate.getTime()-lon_stardate)/1000/3600/24);
				list_dayindexs.get(tempindex).add(i);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}

	
	private int GetSign(Calendar calendar,int inter)
	{
		if(inter==LRQuater) return getSeason(calendar.getTime());
		else if(inter==LRMonth){
			return calendar.get(inter)+1;
		}
		else if(inter==LRYear||inter==LRDate)
		{
			return calendar.get(inter);
		}
		return calendar.get(Calendar.YEAR);
	}
	
	/*
	 * �����������ؿ�ʼ���ڵ��꣨20**�������ȣ�1,2,3,4�����£�1-12����ʾ
	 * Ĭ�Ϸ�����
	 */
	public int Getstartdatesign(int inter)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.startdate);
		return GetSign(calendar, inter);
	}
	 
	public int getEnddatesign(int inter)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.enddate);
		return GetSign(calendar, inter);
	}
	/*
	 * ���þۺϺ�����һ��ʱ���ڵ����ݽ���ͳ��
	 */
	public double[] Statisticbyinter(int inter)
	{	
		ArrayList<ArrayList<Integer>> list_index = new ArrayList<>();
		if(inter==LRDate)
		{
			list_index = list_dayindexs;
		}
		else{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.startdate);
			ArrayList<Integer> templist = new ArrayList<>();
			int sign = GetSign(calendar, inter);
			for(int i = 0;i<list_dayindexs.size();i++)
			{
				templist.addAll(list_dayindexs.get(i));
				calendar.add(Calendar.DATE, 1);
				int nextsign = GetSign(calendar, inter);
				if(nextsign!=sign)
				{
					list_index.add(templist);
					templist = new ArrayList<>();
					sign = nextsign;
				}
			}
		}
		double[] val_sta = new double[list_index.size()];
		for(int i = 0;i<list_index.size();i++)
		{
			val_sta[i] = this.aggregationfun.calculatevalue(lrInstances, list_index.get(i));
		}
		return val_sta;
	}
	/*
	 * ���߼���
	 */
	public double[] StatisticAverage(int num)throws Exception
	{
		int size = list_dayindexs.size()-num+1;
		if(size<=0) throw new Exception("�������ֵ������������");
		ArrayList<ArrayList<Integer>> list_index = new ArrayList<>();
		for(int i = 0;i<size;i++)
		{
			ArrayList<Integer> templist = new ArrayList<>();
			for(int j = 0;j<num;j++)
			{
				templist.addAll(list_dayindexs.get(i+j));
			}
			list_index.add(templist);
		}
		double[] val_sta = new double[list_index.size()];
		for(int i = 0;i<list_index.size();i++)
		{
			val_sta[i] = this.aggregationfun.calculatevalue(lrInstances, list_index.get(i))/num;
		}
		return val_sta;
	}
	
	
	
	 /** 
     *  
     * 1 ��һ���� 2 �ڶ����� 3 �������� 4 ���ļ��� 
     *  
     * @param date 
     * @return 
     */  
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
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			LRInstances lrInstances = LRInstances.loadfromDB("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres", "select * from coalmine");
			lrInstances.setsign_attr_date(3);
			lrInstances.setsign_attr_geolon(1);
			lrInstances.setsign_attr_geolat(2);
			TimeSeriseStatistics statistics = new TimeSeriseStatistics(lrInstances);
			/*double[] result = statistics.StatisticAverage(5);
			int sum = 0;
			for(int i=0;i<result.length;i++)
			{
				sum+=result[i];
				System.out.println(result[i]+",");
				
			}
			System.out.println("sum="+sum);*/
			System.out.println("startdate="+statistics.startdate);
			System.out.println("enddate="+statistics.enddate);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date1 = dateFormat.parse("2000-01-01 00:00:00");
			Date date2 = dateFormat.parse("2004-01-01 00:00:00");
			DateFilter df = new DateFilter(lrInstances, date1,date2);
			LRInstances lrInstances2 = df.LRProcess(lrInstances);
			System.out.println("size="+lrInstances2.instances.numInstances());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
