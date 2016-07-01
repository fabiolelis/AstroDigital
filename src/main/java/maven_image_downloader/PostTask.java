package maven_image_downloader;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

public class PostTask implements Runnable {

	
    public void run() {
    
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			
	
			HttpPost httppost = new HttpPost("https://api.astrodigital.com/v2.0/tasks");
			httppost.addHeader("Authorization", "Token 51fcf1fd2c063aaf3ac22029adf505c2d56e681c");

			
			String jsonParams = "{\"name\":\"Our Arugula Field\","+ 
					  "\"products\": [{\"product\": \"ndvi_image\", \"actions\": [\"mapbox\"]}],"+     
					  "\"query\": {"+
					    "\"date_from\": \"2015-11-01\","+
					    "\"date_to\": \"2016-01-01\","+
					    "\"aoi\": {"+
					      "\"type\": \"Polygon\","+
					      "\"coordinates\": [" +
					        "[" + 
					            "[-122.62664794921874, 38.81403111409755],"+
					            "[-122.62664794921874, 39.07464374293249],"+
					            "[-122.16796875, 39.07464374293249],"+
					            "[-122.16796875, 38.81403111409755],"+
					            "[-122.62664794921874, 38.81403111409755]"+
					        "]"+
					      "]"+
					    "}"+
					  "}"+
					"}";
			
			String strParams = "{\"name\": \"Our Arugula Field\",\"products\": [{\"product\": \"ndvi_image\",\"actions\": [\"mapbox\"]}],\"query\": {\"date_from\": \"2015-11-01\",\"date_to\": \"2016-01-01\",\"aoi\": {\"type\": \"Polygon\",\"coordinates\": [[[-122.62664794921874, 38.81403111409755],[-122.62664794921874, 39.07464374293249],[-122.16796875, 39.07464374293249],[-122.16796875, 38.81403111409755],[-122.62664794921874, 38.81403111409755]]]}}}";
			
			
	        StringEntity params =new StringEntity(strParams);
	        httppost.addHeader("content-type", "application/json");
	        httppost.setEntity(params);
			
	        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	        	public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
	        		int status = response.getStatusLine().getStatusCode();
                    //if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

                    	StringBuffer result = new StringBuffer();
                    	
                    	String line = "";
                    	while ((line = rd.readLine()) != null) {
                    		result.append(line);
                    	}
                    	
                        System.out.println("result scenes: " + result);
	                    return null;
	                        //return entity != null ? EntityUtils.toString(entity) : null;
	              //   } else {
	                //	 throw new ClientProtocolException("Unexpected response status: " + status);
	                 //}
	                    
                }

	        };
    	            
    	    String responseBody = httpclient.execute(httppost, responseHandler);
    	    
		} catch (ClientProtocolException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
    	
    	
    }
}