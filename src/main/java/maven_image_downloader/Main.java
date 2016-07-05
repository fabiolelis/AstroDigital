package maven_image_downloader;


import java.util.List;
import maven_image_downloader.PostTask;
import maven_image_downloader.GetRaws;
import maven_image_downloader.GetPictures;
import maven_image_downloader.GetResults;


public class Main {

	public static void main(String[] args) {
		
		try{
			
			/*args: module (image/task), taskid/taskname, null/jsonfilename
			 * ex1 java -jar maven_image_downloader-0.0.1-SNAPSHOT.jar image 8456 imagesfolder
			 * ex2 java -jar maven_image_downloader-0.0.1-SNAPSHOT.jar task newtaskname 
			 */
			
			if(args[0] == null || args[1] == null) throw new Exception("Irregular args");
			
			String answer = args[0].toString();
			
			if(answer.toLowerCase().equals("image")){
				String imagesFolder = "/Users/fabiolelis/Desktop/space_images/" ;
				if(args.length > 2){
					imagesFolder = args[2];
				}
				String task_id = args[1].toString(); 
				System.out.println("Fetching images for task " + task_id);
				GetResults scenes = new GetResults();  
				scenes.setTask_id(task_id);
				Thread t1 = new Thread(scenes);
				t1.start();
				t1.join();
				List<String> scenesIDs = scenes.getScenesIds();
				List<String> rawsUrls = scenes.getRawsUrls();
				
				//scenesIDs.clear();
				//rawsUrls.add("http://storage.googleapis.com/earthengine-public/landsat/L8/099/066/LC80990662016021LGN00.tar.bz");
				
				if(scenesIDs.size() > 0){
					GetPictures pics = new GetPictures();
					pics.setScenesIds(scenesIDs);	
					pics.setTaskId(task_id);
					pics.setImagesFolder(imagesFolder);
					Thread t2 = new Thread(pics);
					t2.start();
					t2.join();
				}
				
				if(rawsUrls.size() > 0){
					GetRaws getRaws = new GetRaws();
					getRaws.setRawsUrls(rawsUrls);	
					getRaws.setTaskId(task_id);
					getRaws.setImagesFolder(imagesFolder);
					Thread t2 = new Thread(getRaws);
					t2.start();
					t2.join();
				}
				
//				List<String> picsStr = pics.getPics();
				System.out.println("Done");
			}
			else{
				
				System.out.println("To post");
				PostTask posttask = new PostTask();
				
				posttask.setName(args[1].toString());				
				
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
