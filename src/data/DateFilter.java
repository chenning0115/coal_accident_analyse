package data;

import java.util.Date;

import org.geotools.wfs.v2_0.bindings.ReturnFeatureTypesListTypeBinding;

import opendap.dap.test.expr_test;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

/*
 * �������ڹ��˳���������ʼʱ���ڵ�����instance
 */
public class DateFilter extends AbstractILRFilter{

	private Date startdate;
	private Date enddate;
	private LRInstances lrInstances;
	public DateFilter(LRInstances _LrInstances,Date _startdate,Date _enddate) {
		// TODO Auto-generated constructor stub
		this.lrInstances = _LrInstances;
		this.startdate = _startdate;
		this.enddate = _enddate;
	}
	@Override
	protected Instances determineOutputFormat(Instances arg0) throws Exception {
		// TODO Auto-generated method stub
		Instances resultformat = new Instances(arg0, 0);
		return resultformat;
	}

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return "A filter to get get instances between startdate and enddate";
	}

	/*
	 * (non-Javadoc)
	 * @see weka.filters.SimpleFilter#process(weka.core.Instances)
	 * ���ص����������ݵ�ǳ����
	 */
	@Override
	protected Instances process(Instances arg0) throws Exception 
	{
		// TODO Auto-generated method stub
		long date_start = startdate.getTime();
		long date_end = enddate.getTime();
		Instances result = new Instances(determineOutputFormat(arg0), 0);
		for(int i = 0;i<arg0.numInstances();i++)
		{
			long date_ins = lrInstances.GetValueofDate(i).getTime();
			if(date_ins>=date_start&&date_ins<=date_end)
			{
				result.add(arg0.get(i));
			}
		}
	     return result;
	}

	 public Capabilities getCapabilities() {
	     Capabilities result = super.getCapabilities();
	     result.enableAllAttributes();
	     result.enableAllClasses();
	     result.enable(Capability.NO_CLASS); // filter doesn't need class to be set
	     return result;
	   }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public LRInstances LRProcess(LRInstances _lLrInstances){
		// TODO Auto-generated method stub
		Instances data = _lLrInstances.instances;
		Instances data2 = null;
		try {
			data2 = this.process(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LRInstances lrInstances2 = new LRInstances(data2);
		lrInstances2.setsign_attr_date(lrInstances2.getsign_attr_date());
		lrInstances2.setsign_attr_geolat(lrInstances2.getsign_attr_geolat());
		lrInstances2.setsign_attr_geolon(lrInstances2.getsign_attr_lng());
		return lrInstances2;
	}
	@Override
	protected Instances lrprocess(Instances data) {
		// TODO Auto-generated method stub
		try {
			return this.process(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
