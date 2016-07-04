package maven_image_downloader;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetResults implements Runnable {

	private volatile List<String> scenesIDs = new ArrayList<String>();
	private volatile List<String> rawsUrls = new ArrayList<String>();
	private volatile String task_id;
	
	
	public List<String> getScenesIds(){
		return scenesIDs;
	}
	public List<String> getRawsUrls(){
		return rawsUrls;
	}
	public void setTask_id(String task_id){
		this.task_id = task_id;
	}
	
    public void run() {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("https://api.astrodigital.com/v2.0/results?task_id=" + task_id);
            httpget.addHeader("Authorization", "Token 51fcf1fd2c063aaf3ac22029adf505c2d56e681c");
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        
                        //System.out.println("Response success");
                       
                        BufferedReader rd = new BufferedReader(
                    	        new InputStreamReader(entity.getContent()));

                    	StringBuffer result = new StringBuffer();
                    	String line = "";
                    	while ((line = rd.readLine()) != null) {
                    		result.append(line);
                    	}
                        
                    	System.out.println("result: " + result);
                    	JSONObject obj = new JSONObject(result.toString());
                    	JSONArray arr = obj.getJSONArray("results");
                    	String scenesIDStr = "";
                    	
                    	for (int i = 0; i < arr.length(); i++)
                    	{
                    	    String resultStatus = arr.getJSONObject(i).getString("status");
                    	    if(resultStatus.equals("COMPLETED")){
                    	    	if(arr.getJSONObject(i) != null && arr.getJSONObject(i).getJSONObject("value") != null && arr.getJSONObject(i).getJSONObject("value").getJSONObject("meta") != null){
                    	    		String scene = arr.getJSONObject(i).getJSONObject("value").getJSONObject("meta").getString("scene_id");
                            	    if(scene != null){
                            	    	scenesIDs.add(scene);
                            	    	scenesIDStr += scene + "\n";
                            	    }
                            	  //  System.out.println(scene);
                    	    	}
                    	    	else if(arr.getJSONObject(i).getJSONObject("value") != null){
                    	    		String raw_url = arr.getJSONObject(i).getJSONObject("value").getString("raw_image_url");
                    	    		rawsUrls.add(raw_url); 

                    	    	}
                        	    
                    	    }
                    	    
                    	}
                    	
                    	
                    	return scenesIDStr;
                        //return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                    
                    
                    
                }

            };
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            //System.out.println("----------------------------------------");
            //System.out.println(responseBody);
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
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