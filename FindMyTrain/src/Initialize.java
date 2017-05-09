

/**
 * When Server begins, Initialise is called...
 * It call the Query Handler every minute...
 * */


import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Author
 *
 *   █████╗ ██╗   ██╗██████╗  ██████╗ 
 *  ██╔══██╗██║   ██║██╔══██╗██╔═══██╗
 *  ███████║██║   ██║██████╔╝██║   ██║
 *  ██╔══██║██║   ██║██╔══██╗██║   ██║
 *  ██║  ██║╚██████╔╝██║  ██║╚██████╔╝
 *  ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ 
 **/

public class Initialize implements ServletContextListener {
	static long updateTime = 10000;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
        /** This method is called when the server shuts down !!!! */
		System.out.println("Server Shutting Down!!!");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

        /** Inialise data structures on server startup!!!! */
		System.out.println("System Startup!!!! Analyzing Data to detect location!!!");
		Spotting.trnSpotting.add(new CopyOnWriteArrayList<Spotting>());
		Spotting.trnSpotting.add(new CopyOnWriteArrayList<Spotting>());
		Spotting.trnSpotting.add(new CopyOnWriteArrayList<Spotting>());
		
		query.start();
	}
	
    /** Thread  query is responsible for running the Query Handler (FindMyTrainCore in report) every minute */
	Thread query = new Thread(new Runnable() {
		QueryHandler q = new QueryHandler();
		public void run() {
			while (true) {
				try {
					Thread.sleep(updateTime);
				} catch (InterruptedException e) {System.out.println("Thread Timer exception!!!!");}
				
                /** If no data in any of the route database, cannot work!!!! */
				if(!(Spotting.trnSpotting.get(0).isEmpty() && Spotting.trnSpotting.get(1).isEmpty() && Spotting.trnSpotting.get(2).isEmpty()))	{
					SpottingNow.trnSpottingNow.clear();
					PositionConfidence.posnConf.clear();
					q.getSpottingsNow();
					q.getPositionConfidence();
					q.computePeaks();
				}
				else	{
					System.out.println(System.currentTimeMillis()+" : Spotting Database Empty!!!!");
				}
			}
		}
	});
}
