import java.util.ArrayList;

/**
 * IN CASE OF DIRECTION :
 * 		1 : Up
 * 		0 : Down
 */

class StaticData {
	static double trainSpeed = 10.205;	// m/s
	static double haltTime = 20;	// sec
	static int numRoutes = 3;	
}

/** Station Information */
class Station 
{
	public int routeID;	
	public int stnID;
	public String stnName;
	public double lat, lon;
	public double nxtStnDist;
	public double distOrigin;
	public static double[] routeLength = {123780.0, 54000.0,49000.0};
	
	public Station(int routeID, int stnID, String stnName, double lat, double lon, double nxtStnDist,
			double distOrigin) {
		this.routeID = routeID;
		this.stnID = stnID;
		this.stnName = stnName;
		this.lat = lat;
		this.lon = lon;
		this.nxtStnDist = nxtStnDist;
		this.distOrigin = distOrigin;
	}
	
	static String[] routeName = new String[StaticData.numRoutes];
	static ArrayList<ArrayList<Station>> routeList = new ArrayList<ArrayList<Station>>();
}

