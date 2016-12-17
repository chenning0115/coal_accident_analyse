package analyse.association;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

public class LRRule {

	public HashMap<String,Double> metricmap;
	public String premise;
	public String consequense;
	
	public LRRule(HashMap<String,Double> _map,String _premise,String _consequense)
	{
		this.metricmap = _map;
		this.premise = _premise;
		this.consequense = _consequense;
		
	}
	
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(premise+","+consequense);
		Set<String> keys = metricmap.keySet();
		for (String string : keys) {
			buffer.append(string+"="+metricmap.get(string));
		}
		return buffer.toString();
	}
}
