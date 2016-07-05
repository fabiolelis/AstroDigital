package maven_image_downloader;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;


public class GetRaws implements Runnable {

	private volatile List<String> rawsUrls = new ArrayList<String>();
	private volatile List<String> pics = new ArrayList<String>();
	private volatile String task_id;
	private volatile String imagesFolder;

	
	public void setRawsUrls(List<String> rawsUrls){
		this.rawsUrls = rawsUrls;
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
		
		try {
			
			for(String strurl : rawsUrls){

				
            	File fdir = new File(imagesFolder+task_id);
        		if(!fdir.exists() && !fdir.isDirectory()) {
        			fdir.mkdir();
        		}
        		String spliturl[] = strurl.split("/");
        		String filename = spliturl[spliturl.length - 1];
        		File fileoutput = new File(imagesFolder+task_id+"/"+filename);
				
				URL url = new URL(strurl);
            	InputStream in = new BufferedInputStream(url.openStream());
            	FileOutputStream out = new FileOutputStream(fileoutput);
            	byte[] buf = new byte[1024];
            	int n = 0;
            	System.out.println("Saving in " + imagesFolder +task_id+"/"+filename);
            	while (-1!=(n=in.read(buf)))
            	{
            	   out.write(buf, 0, n);
            	}
            	out.close();
            	in.close();
            	System.out.println("Done");
			
        		
        		System.out.println("Uncompressing raw...");
            	File fdest = new File(imagesFolder+ task_id);

            	uncompressTarGZ(fileoutput, fdest);
        		System.out.println("Done");
			
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		   
		}	
    	
    }
    public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
        System.out.println("length " +tarFile.length());
    	
    	//dest.mkdir();
        TarArchiveInputStream tarIn = null;

        tarIn = new TarArchiveInputStream(
                    new BZip2CompressorInputStream(
                        new BufferedInputStream(
                            new FileInputStream(
                                tarFile
                            )
                        )
                    )
                );
        

        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        // tarIn is a TarArchiveInputStream
        while (tarEntry != null) {// create a file with the same name as the tarEntry
            File destPath = new File(dest, tarEntry.getName());
            System.out.println("working: " + destPath.getCanonicalPath());
            if (tarEntry.isDirectory()) {
                destPath.mkdirs();
            } else {
                destPath.createNewFile();
                //byte [] btoRead = new byte[(int)tarEntry.getSize()];
                byte [] btoRead = new byte[1024];
                //FileInputStream fin 
                //  = new FileInputStream(destPath.getCanonicalPath());
                BufferedOutputStream bout = 
                    new BufferedOutputStream(new FileOutputStream(destPath));
                int len = 0;

                while((len = tarIn.read(btoRead)) != -1)
                {
                    bout.write(btoRead,0,len);
                }

                bout.close();
                btoRead = null;

            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    } 
}