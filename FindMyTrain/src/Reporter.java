

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

@WebServlet("/Reporter")
public class Reporter extends HttpServlet {
	public static final Lock lock = new ReentrantReadWriteLock().writeLock();
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Welcome to FindMyTrain Query Reporter!!!");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Hello");
		String result="";
		int routeID = Integer.parseInt(request.getReader().readLine());
		System.out.println(routeID);
		ArrayList<Double> posCon = new ArrayList<Double>();
		double max=0, min=0;
		
//		for (ArrayList<ArrayList<PositionConfidence>> pCon : PositionConfidence.posnConf) {
//				for (PositionConfidence p : pCon.get(routeID)) {
//					System.out.println(p.distNow+", "+p.posConf + ", " + p.isPeak);
//				}
//			System.out.println("-------------------");
//		}
		
        /** Get range in which train lies */
		for (ArrayList<ArrayList<PositionConfidence>> pCon : PositionConfidence.posnConf) {
			for (PositionConfidence pc : pCon.get(routeID)) 
				if(pc.isPeak)	posCon.add(pc.distNow);
			if(posCon.size()>=1)	{
				min=posCon.get(0);
				max = min;
				for (int i = 1; i < posCon.size(); i++) {
					if(posCon.get(i)-posCon.get(i-1)>500){
						result += min + " --- > " + max + ", ";
						min=posCon.get(i);
						max=min;
					}
					else	max=posCon.get(i);
				}
			}
			posCon.clear();
		}
		result = result==""?result += min + " --- > " + max:result;
			
		System.out.println(result);
		response.getWriter().append(result);
	}
}
