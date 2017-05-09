package simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.io.InputStream;

public class RequestHandler {
	public String sendPostRequest(final String requestURL, final String data) {
        final String[] ret = {""};

        /******* Requires a separate thread to send data to server ******/
        Thread postThread = new Thread(new Runnable() {
            URL url;
            String response = "";

            @Override
            public void run() {
                try {
                    url = new URL(requestURL);

                    /****** Creates the connection ******/
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    /**** Write data to server *****/
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    os.close();

                    /**** Recieve response from server ******/
                    int responseCode = conn.getResponseCode();

                    /****** Check validity of the response ******/
                    if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        response = br.readLine();
                    }
                    else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND)
                        response = "NULL";
                    else
                        response = "Error Registering";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ret[0] = response;
                //Log.d("as1",ret[0]);
               // Log.d("Response: ", response==null?"Sorry!!! No data to Analyze!!!":response + " || " + DataCollector.GPSLat+","+DataCollector.GPSLong);
            }
        });

        postThread.start();

        try {
                postThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.d("as2", "sdfds");
       // Log.d("as3", "akask" + ret[0]);
       // Log.d("as4", String.valueOf(ret.length));
        /***** Return the response *****/
        return ret[0];
    }
}
