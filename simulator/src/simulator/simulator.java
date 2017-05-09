package simulator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class simulator {
	
	
	public static void main(String[] args) throws IOException, JSONException {
		System.out.println("Inside service");
		JSONObject jObj;
        JSONArray jArray = new JSONArray();
        String fname = "time_dist_lat_lon";
        FileReader fr = null;
		try {
			fr = new FileReader(fname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader(fr);
        
        int seconds=0;
        int cnt=0;
        String line = br.readLine();
        while(true){
        	if(cnt>0 && cnt%60==0){
        		JSONObject data = new JSONObject();
        		data.put("data", jArray);
        		String tosend = data.toString();
        		RequestHandler request = new RequestHandler();
        		request.sendPostRequest("http://10.129.28.97:8007/FindMyTrain/RequestHandler", tosend);
        		jArray = new JSONArray();
        		
        		
        	}
        	String[] a = line.split(",",0);
        	if(Integer.parseInt(a[0])==cnt){
        		jObj = new JSONObject();
	            jObj.put("lat",Double.parseDouble(a[2]));
	            jObj.put("lon",Double.parseDouble(a[3]));
	            jObj.put("dist",Double.parseDouble(a[1]));
	            jObj.put("routeID",1);
	            jObj.put("direction",1);
	            jObj.put("timeStamp",0);
	            jArray.put(jObj);
	            line=br.readLine();
        	}

        	cnt = cnt+1;
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(line==null) break;
        }
        
        
	}

	

}