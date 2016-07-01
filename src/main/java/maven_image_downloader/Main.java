package maven_image_downloader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		try{
			System.out.println("1. Download images\n2. Post new task");
			Scanner in = new Scanner(System.in);
			int answer = in.nextInt();
			
			if(answer == 1){
				System.out.println("Fetching images...");
				GetResults scenes = new GetResults();  
				Thread t1 = new Thread(scenes);
				t1.start();
				t1.join();
				List<String> scenesIDs = scenes.getScenesIds();
				//System.out.println("retornou (0): " + scenesIDs.get(0));
				
				GetPictures pics = new GetPictures();
				pics.setScenesIds(scenesIDs);
				Thread t2 = new Thread(pics);
				t2.start();
				t2.join();
				
				List<String> picsStr = pics.getPics();
				System.out.println("Done");
			}
			else{
				System.out.println("To post");
				PostTask newTask = new PostTask();  

				Thread t3 = new Thread(newTask);
				t3.start();
				t3.join();
				System.out.println("Done");
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	 

}
