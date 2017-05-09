

/**
 * QueryHandler is called FindMyTrainCore in presentation...
 * It calculates curent location of train with inputs from users 
 * */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class QueryHandler {
    
    /** For each spotting, Find where the spotting correspond to at timeNow() */
	protected void getSpottingsNow() {

		long timeNow = System.currentTimeMillis();
		double confidence, distNow, distTravelled = 0;
		ArrayList<ArrayList<SpottingNow>> trnSpottingNowUP = new ArrayList<ArrayList<SpottingNow>>(),
										  trnSpottingNowDOWN = new ArrayList<ArrayList<SpottingNow>>();
		ArrayList<SpottingNow> tsNowUp, tsNowDown;
        
        /** Traverse for spottings across every route */
		for (CopyOnWriteArrayList<Spotting> sRoute : Spotting.trnSpotting) {
			tsNowUp = new ArrayList<SpottingNow>();
			tsNowDown = new ArrayList<SpottingNow>();
			/** Traverse every spottings of a route */
            for (Spotting ts : sRoute) {
                /** Get probability from how past the spotting is */
				confidence = getConfidenceFromPast(ts.timeStamp, timeNow);
				if (confidence > 0) {
                    /** Find the distance travelled by the user since the user input was given */
					distTravelled = findDistanceTravelled(ts, timeNow);
					if (distTravelled < 0)	continue;
					distNow = ts.direction == 1 ? ts.dist + distTravelled : ts.dist - distTravelled;
					System.out.println(ts.dist+ " + " + distTravelled + " = " + distNow);
					/** Insert new positions at time = timeNow() in new DataStructure trainspotingNow */
                    if (distNow > 0 && distNow < Station.routeLength[ts.route]) {
						if (ts.direction == 1)
							tsNowUp.add(new SpottingNow(ts.userID, distNow, ts.route, ts.direction, timeNow, confidence));
						else
							tsNowDown.add(new SpottingNow(ts.userID, distNow, ts.route, ts.direction, timeNow, confidence));
					}
				}
			}
			trnSpottingNowUP.add(tsNowUp);
			trnSpottingNowDOWN.add(tsNowDown);
		}
		SpottingNow.trnSpottingNow.add(trnSpottingNowUP);
		SpottingNow.trnSpottingNow.add(trnSpottingNowDOWN);
	}
    

    /** Get probablity of trains in every 100 m segments along routes */
	protected void getPositionConfidence() {

		double confDist, overAllConf, tempConf = 0;
		int direction = 1;
		ArrayList<SpottingNow> sRouteNow;
		ArrayList<PositionConfidence> tempPosnConf = null;
		ArrayList<ArrayList<PositionConfidence>> tempPosnConfDir = null;

		for (ArrayList<ArrayList<SpottingNow>> sRouteDirection : SpottingNow.trnSpottingNow) {
			tempPosnConfDir = new ArrayList<ArrayList<PositionConfidence>>();
			for (int i = 0; i < sRouteDirection.size(); i++) {
				tempPosnConf = new ArrayList<PositionConfidence>();
				sRouteNow = sRouteDirection.get(i);
				for (int j = 0; j < Station.routeLength[i]; j += 100) {
					tempConf = 1;
					for (SpottingNow tsNow : sRouteNow) {
                        /** Get confidence based on distance */
						if (Math.abs(tsNow.distNow - j) < 2000) {
							confDist = getConfidenceFromDistance(Math.abs(tsNow.distNow - j));
							overAllConf = tsNow.confidence * confDist;
							tempConf *= (1 - overAllConf);
						}
					}
					tempPosnConf.add(new PositionConfidence(j, (1 - tempConf), true, direction));
				}
				tempPosnConfDir.add(tempPosnConf);
			}
			direction = 0;
			PositionConfidence.posnConf.add(tempPosnConfDir);
		}
	}

    /** Compute peaks from position confidence */
	protected void computePeaks() {
		PositionConfidence psC = null;
		int jStart, jEnd;
        
        /** Delete all such peaks which has any other peak in a range of distance */
		for (ArrayList<ArrayList<PositionConfidence>> pcRouteDir : PositionConfidence.posnConf) {
			for (int i = 0; i < pcRouteDir.size(); i++) {
				for (int j = 0; j < pcRouteDir.get(i).size(); j++) {
					psC = pcRouteDir.get(i).get(j);
					if (psC.posConf < 0.1) {
						psC.isPeak = false;
						continue;
					}
					if (j < PositionConfidence.peakThres)	jStart = 0;
					else	jStart = j - PositionConfidence.peakThres;

					if (j + PositionConfidence.peakThres >= pcRouteDir.get(i).size())
						jEnd = pcRouteDir.get(i).size() - 1;
					else	jEnd = j + PositionConfidence.peakThres;

					for (int k = jStart; k <= jEnd; k++) {
						if (psC.posConf < pcRouteDir.get(i).get(k).posConf) {
							psC.isPeak = false;
							break;
						}
					}
				}
			}
		}
	}

    /** Get the distance travelled by the user since its input time */
	public double findDistanceTravelled(Spotting ts, long timeNow) {

		String sql;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		double distPrev = ts.dist, dist = -1, timeInterval = 0, distTravelled = 0;
		long timeDiff = timeNow / 1000 - ts.timeStamp / 1000;
		try {
			/** Connect to the database **/
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost/locationDB", "root", "qscguk6*");
			stmt = conn.createStatement();
			while (timeDiff > StaticData.haltTime) {
				if (ts.direction == 1)
					sql = "select * from (select * from stationMAP where distOrigin > " + distPrev + " and routeID ="+ts.route + ") as dist order by distOrigin asc limit 1;";
				else
					sql = "select * from (select * from stationMAP where distOrigin < " + distPrev + " and routeID = "
							+ ts.route + ") as dist order by distOrigin desc limit 1;";

				/** Get Result **/
				rs = stmt.executeQuery(sql);
				
				if(rs.first())	 dist = rs.getDouble(7);
				else {
					try {
						if(conn!=null)	
							conn.close();
						if(stmt!=null)
							stmt.close();
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("no rows");
					return -1;
				}

				timeInterval = Math.abs(dist - distPrev) / StaticData.trainSpeed;
				if ((timeDiff - timeInterval) > 0) {
					timeDiff -= timeInterval;
					distTravelled += Math.abs(dist - distPrev);
				} 
				else break;
				distPrev = dist;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn!=null)	
					conn.close();
				if(stmt!=null)
					stmt.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (dist == -1)	return -1;

		if (timeDiff > StaticData.haltTime)	return distTravelled + (timeDiff - StaticData.haltTime) * StaticData.trainSpeed;
		else	return distTravelled;
	}

	public double getConfidenceFromPast(long t, long nowtime) {
		if (t > nowtime)	return -1;
		long timeDifference = nowtime - t;
		if (timeDifference < 60000)			return 1;
		if (timeDifference < 300000)		return 0.9;
		if (timeDifference < 900000)		return 0.8;
		if (timeDifference > 1800000)		return 0;
		return (0.8 - 0.8 * timeDifference / 1800000);
	}

	double getConfidenceFromDistance(double diffm) {
		if (diffm < 100)		return 1;
		if (diffm < 200)		return 0.9;
		if (diffm < 400)		return 0.8;
		if (diffm < 800)		return 0.6;
		if (diffm < 1200)		return 0.3;
		if (diffm > 2000)		return 0;
		return (0.3 - 0.3 * diffm / (2000));
	}
}
