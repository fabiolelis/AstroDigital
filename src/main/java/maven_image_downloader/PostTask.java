package maven_image_downloader;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;





public class PostTask implements Runnable {

	private volatile String name;
	private volatile String filepath;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	
	
    public void run() {
    
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			filepath = "/Users/fabiolelis/Desktop/Space/geometry2.json";
			BufferedReader br = new BufferedReader(new FileReader(filepath));
        	StringBuffer result = new StringBuffer();
        	String line = "";
        	while ((line = br.readLine()) != null) {
        		result.append(line);
        	}
            
        	System.out.println("result: " + result);
        	JSONObject polygon = new JSONObject(result.toString());
        	
        	//Geometry geom = g.read("{\"type\": \"Polygon\",\"coordinates\": [[[100.0, 0.0],[101.0, 0.0],[101.0, 1.0],[100.0, 1.0],[100.0, 0.0]]]}");
        	
        	
			GeometryJSON g = new GeometryJSON(15);		
			Geometry geom = g.read(result.toString());
			
		    System.out.println(geom);

		    StringWriter sw = new StringWriter();
		    try {
		        g.write(geom, sw);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    String aoi = sw.toString();
		    
	        System.out.println("geometry: " + aoi);

        	
			HttpPost httppost = new HttpPost("https://api.astrodigital.com/v2.0/tasks");
			httppost.addHeader("Authorization", "Token 51fcf1fd2c063aaf3ac22029adf505c2d56e681c");
			
			String strParams = "{\"name\": \"" + name +"\","
					+ "\"products\": [{\"product\": \"ndvi_image\",\"actions\": [\"mapbox\",\"raw\"]}],"
					+ "\"query\": {\"date_from\": \"2015-11-01\",\"date_to\": \"2016-01-01\","
					+ "\"aoi\": " + aoi
					+ "}}";
			System.out.println(strParams);

			
			
	        StringEntity params =new StringEntity(strParams);
	        httppost.addHeader("content-type", "application/json");
	        httppost.setEntity(params);
			
	        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	        	public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
	        		int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
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
	                 } else {
	                	 throw new ClientProtocolException("Unexpected response status: " + status);
	                 }
	                    
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