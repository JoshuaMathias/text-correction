package spelling;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryCorrection {

	public String origQuery="";
	public String newQuery="";
	public ArrayList<String> soundexes = new ArrayList<String>();
	public HashMap<String,ArrayList<String>> corrections = new HashMap<String,ArrayList<String>>();
	
	public QueryCorrection() {
		
	}

}
