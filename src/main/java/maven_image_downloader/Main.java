package maven_image_downloader;


import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		try{
			
			/*args: module (image/task), taskid/taskname, null/jsonfilename
			 * ex1 java -jar maven_image_downloader-0.0.1-SNAPSHOT.jar image 8456
			 * ex2 java -jar maven_image_downloader-0.0.1-SNAPSHOT.jar task newtaskname /Users/fabiolelis/Desktop/Space/geometry.json
			 */
			
			if(args[0] == null || args[1] == null) throw new Exception("Irregular args");
			
			String answer = args[0].toString();
			
			if(answer.toLowerCase().equals("image")){
				
				String task_id = args[1].toString(); 
				System.out.println("Fetching images for task " + task_id);
				GetResults scenes = new GetResults();  
				scenes.setTask_id(task_id);
				Thread t1 = new Thread(scenes);
				t1.start();
				t1.join();
				List<String> scenesIDs = scenes.getScenesIds();
				List<String> rawsUrls = scenes.getRawsUrls();
				
				scenesIDs.clear();
				rawsUrls.add("http://storage.googleapis.com/earthengine-public/landsat/L8/099/066/LC80990662016021LGN00.tar.bz");
				
				if(scenesIDs.size() > 0){
					GetPictures pics = new GetPictures();
					pics.setScenesIds(scenesIDs);	
					pics.setTaskId(task_id);
					Thread t2 = new Thread(pics);
					t2.start();
					t2.join();
				}
				
				if(rawsUrls.size() > 0){
					GetRaws getRaws = new GetRaws();
					getRaws.setRawsUrls(rawsUrls);	
					getRaws.setTaskId(task_id);
					Thread t2 = new Thread(getRaws);
					t2.start();
					t2.join();
				}
				
//				List<String> picsStr = pics.getPics();
				System.out.println("Done");
			}
			else{
				if(args[2] == null) throw new Exception("Irregular args");
				
				System.out.println("To post");
				PostTask posttask = new PostTask();
				
				posttask.setName(args[1].toString());
				posttask.setFilepath(args[2].toString());
				
				
				Thread t3 = new Thread(posttask);
				t3.start();
				t3.join();
				System.out.println("Done");
		        
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	
	 

}
