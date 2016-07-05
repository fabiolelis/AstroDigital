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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetPictures implements Runnable {

	private volatile List<String> scenesIDs = new ArrayList<String>();
	private volatile List<String> pics = new ArrayList<String>();
	private volatile String task_id;
	private volatile String imagesFolder;
	
	public void setScenesIds(List<String> scenesIDs){
		this.scenesIDs = scenesIDs;
	}
	
	public List<String> getPics(){
		return pics;
	}
	public void setTaskId(String task_id){
		this.task_id = task_id;
	}
	public void setImagesFolder(String imagesFolder){
		this.imagesFolder = imagesFolder;
	}
	
	
    public void run() {
    	
    	pics = new ArrayList<String>();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			
			for(String sceneID : scenesIDs){

    			HttpGet httpget = new HttpGet("https://api.astrodigital.com/v2.0/search?scene_id=" + sceneID);
    			
    	        
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
	                    	
	                    //	System.out.println("result scenes: " + result);

	                    	JSONObject obj = new JSONObject(result.toString());

	                    	JSONArray results =  obj.getJSONArray("results");
	                    	String pic = ((JSONObject)results.get(0)).getString("browseURL");
	                    	String sceneID = ((JSONObject)results.get(0)).getString("sceneID");

	                    	
	                    	URL url = new URL(pic);
	                    	InputStream in = new BufferedInputStream(url.openStream());
	                    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	                    	byte[] buf = new byte[1024];
	                    	int n = 0;
	                    	while (-1!=(n=in.read(buf)))
	                    	{
	                    	   out.write(buf, 0, n);
	                    	}
	                    	out.close();
	                    	in.close();
	                    	byte[] bynary = out.toByteArray();
	                    	
	                    	File fdir = new File(imagesFolder+task_id);
                    		if(!fdir.exists() && !fdir.isDirectory()) {
                    			fdir.mkdir();
                    		}
	                    	File f = new File(imagesFolder+task_id+"/"+sceneID+".png");
	                    	if(!f.exists() && !f.isDirectory()) { 
	                    		FileOutputStream fos = new FileOutputStream(imagesFolder+task_id+"/"+sceneID+".png");
		                    	fos.write(bynary);
		                    	fos.close();
		                    	System.out.println("Saved " + sceneID);
	    	                    pics.add(pic);   
	                    	}
	                    	
	                    	 
    	                        
    	                    return null;
    	                        //return entity != null ? EntityUtils.toString(entity) : null;
    	                 } else {
    	                	 throw new ClientProtocolException("Unexpected response status: " + status);
    	                 }
    	                    
	                }

    	        };
    	            
    	        String responseBody = httpclient.execute(httpget, responseHandler);
    	        //System.out.println("----------------------------------------");
    	        //System.out.println(responseBody);
    		}
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