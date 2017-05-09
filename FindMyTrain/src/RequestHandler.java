

/**
 * Servlet to store location information 'spottings' from users */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Author
 *
 *   █████╗ ██╗   ██╗██████╗  ██████╗ 
 *  ██╔══██╗██║   ██║██╔══██╗██╔═══██╗
 *  ███████║██║   ██║██████╔╝██║   ██║
 *  ██╔══██║██║   ██║██╔══██╗██║   ██║
 *  ██║  ██║╚██████╔╝██║  ██║╚██████╔╝
 *  ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ 
 *
 **/

@WebServlet("/RequestHandler")
public class RequestHandler extends HttpServlet {
	public static final Lock lock = new ReentrantReadWriteLock().writeLock();
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().print("This is a GET request");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/** Add JSONdata to Database */
		int nRows = addSpotting(request);
//		System.out.println("Number of Rows Appended : " + nRows);

		/** Respond to Client with Number of rows appended!!! */
		response.getWriter().print("Number of Rows Appended : " + nRows);
	}
		
	public int addSpotting(HttpServletRequest request)	{
		
		/** Read the JSONObject sent by Client to a String */
		String jsonData = "";
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jsonData += line;
		} catch (Exception e) {}
		
		/** Parse JSONArray and add  to Database */
		System.out.println("Data"+jsonData);
		int id=0, route, dir;
		long time, oldTime=0, currentTime=System.currentTimeMillis();
		double lat, lon, dist, oldDist=0;
		JsonReader jsonReader = Json.createReader(new StringReader(jsonData));
		JsonObject j = jsonReader.readObject();
		
		JsonArray jarray = j.getJsonArray("data");
		// --------- //
		if(jarray.size()==0)
			return 0;
		// -------- //
		
		long t = jarray.getValuesAs(JsonObject.class).get(jarray.size()-1).getJsonNumber("timeStamp").longValue();
		System.out.println(t);
		
        /** Synchronization required (Reader-Writer problem)*/
		lock.lock();
		for (JsonObject loc : jarray.getValuesAs(JsonObject.class)) {
//	        id = Integer.parseInt(loc.getString("id"));
		    lat = loc.getJsonNumber("lat").doubleValue();
		    lon = loc.getJsonNumber("lon").doubleValue();
		    dist = loc.getJsonNumber("dist").doubleValue();
		    route = loc.getInt("routeID");
		    dir = loc.getInt("direction");
		    time = currentTime-(t-loc.getJsonNumber("timeStamp").longValue());
		    System.out.println("Data Recieved : "+lat+", "+lon+", "+dist+", "+route+", "+dir+", "+time+", "+Math.abs(dist-oldDist)*1000/Math.abs(time-oldTime));
		    Spotting.trnSpotting.get(route).add(new Spotting(id, lat, lon, dist, route, dir, time));
		    oldDist = dist;
		    oldTime = time;
	    }
		lock.unlock();
		System.out.println("done");
		return jarray.size();	
	}
}
