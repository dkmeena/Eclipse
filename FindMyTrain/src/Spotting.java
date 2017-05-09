

/**
 * Contains Data Structure definations */

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


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


/** User input */
class Spotting {
	public double lat, lon, dist;
	public int userID, route, direction;
	long timeStamp;
	
	public Spotting(int userID, double lat, double lon, double dist, int route, int direction, long timeStamp) {
		this.userID = userID;
		this.lat = lat;
		this.lon = lon;
		this.dist = dist;
		this.route = route;
		this.direction = direction;
		this.timeStamp = timeStamp;
	}
	
	public static CopyOnWriteArrayList<CopyOnWriteArrayList<Spotting>> trnSpotting = new CopyOnWriteArrayList<CopyOnWriteArrayList<Spotting>>(); 
}

/** user input corresponding to current time */
class SpottingNow	{
	public int userID, route, direction;
	long timeStamp;
	public double distNow, confidence;
	
	public SpottingNow(int userID, double distNow, int route, int direction, long timeStamp, double confidence) {
		this.userID = userID;
		this.route = route;
		this.direction = direction;
		this.timeStamp = timeStamp;
		this.distNow = distNow;
		this.confidence = confidence;
	}
	public static ArrayList<ArrayList<ArrayList<SpottingNow>>> trnSpottingNow = new ArrayList<ArrayList<ArrayList<SpottingNow>>>();
}

/** Confidence for every 100 m segment */
class PositionConfidence	{
	double posConf, distNow;	
	public boolean isPeak;
	public int direction;
	
	public static int peakThres = 20;
	
	public PositionConfidence(double distNow, double posConf, boolean isPeak, int direction) {
		this.posConf = posConf;
		this.distNow = distNow;
		this.isPeak = isPeak;
		this.direction = direction;
	}
	
	public static ArrayList<ArrayList<ArrayList<PositionConfidence>>> posnConf = new ArrayList<ArrayList<ArrayList<PositionConfidence>>>();
}
