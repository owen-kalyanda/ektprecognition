package Main;
import static spark.Spark.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import javax.tools.JavaCompiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Main.Train;
import functions.CrossingsWeb;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import spark.utils.IOUtils;

public class Main {
	
	
	
	static Map<String, ArrayList<ArrayList<String>>> models = new HashMap<>();
	static Map<String, ArrayList<String>> modelimage = new HashMap<>();
	
	private static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = javax.xml.bind.DatatypeConverter.printBase64Binary(bytes);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }
	
    public static void main(String[] args) {
    	
    	externalStaticFileLocation("templates");
    	
        get("/", (req, res) -> 
        {
            return new ModelAndView(new HashMap(), "templates/index.vtl");
          }, new VelocityTemplateEngine());
        
        get("/train", (req, res) -> 
        {
            return new ModelAndView(new HashMap(), "templates/train.vtl");
          }, new VelocityTemplateEngine());
        
        post("/api/processEktp", (req, res) -> {
        	long startTime = System.currentTimeMillis();
        	File index = new File("charactersUji");
        	index.mkdir();
        	
        	String imgName ="";
        	ArrayList<ArrayList<String>> hasilRecog = new ArrayList<>();
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("uji"));
			try {
				FileOutputStream fos = null;
				Part filePart=null;
			    
			    System.out.println(req.body());
			    String imageString = req.body();
			    imageString = imageString.substring("data:image/png;base64,"
			            .length());
			    byte[] decodedData = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageString);
			    String datetime = LocalDateTime.now().toString();
			    datetime = datetime.replace(":", ";");
			    imgName = "KTP "+datetime+".jpg";
			    fos = new FileOutputStream("uji\\"+imgName);
			    fos.write(decodedData);
			    fos.close();
			    Uji uji = new Uji();
			    File file = new File("uji\\"+imgName);
			    hasilRecog = uji.proses(file);
			   
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			ArrayList<ArrayList<String>> ready = new ArrayList<>();
			ArrayList<String> ready1 = new ArrayList<>();
			ready1.add("ready");
			ready.add(ready1);
			models = new HashMap<>();
			models.put("info", hasilRecog);
			models.put("ready", ready);
		
		    Gson gson = new Gson();
		    long stopTime = System.currentTimeMillis();
	        long elapsedTime = stopTime - startTime;
	        System.out.println(elapsedTime);
            return gson.toJson(models);
        });
        
        post("/api/trainEktp", (req, res) -> {
        	String imgName ="";
        	ArrayList<BufferedImage> imgsChar = new ArrayList<>();
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("train"));
			try {
				FileOutputStream fos = null;
				Part filePart=null;
			    
			    System.out.println(req.body());
			    String imageString = req.body();

			    imageString = imageString.substring("data:image/png;base64,"
			            .length());
			    byte[] decodedData = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageString);
			    String datetime = LocalDateTime.now().toString();
			    datetime = datetime.replace(":", ";");
			    imgName = "KTP "+datetime+".jpg";
			    fos = new FileOutputStream("train\\"+imgName);
			    fos.write(decodedData);
			    fos.close();
			    Train tr = new Train();
			    File file = new File("train\\"+imgName);
			    
			    ArrayList<String> arrEncod = new ArrayList<>();
			    imgsChar = tr.train(file);
			    for (int j = 0; j < imgsChar.size(); j++) {
			    	File f =  new File("characters\\charcomponent-"+j+".png");
			    	String encodstring = "data:image/png;base64," + encodeFileToBase64Binary(f);
			    	arrEncod.add(encodstring);
				}
			    
			    ArrayList<String> ready = new ArrayList<>();
				ready.add("ready");
			    modelimage = new HashMap<>();
			    modelimage.put("chars", arrEncod);
			    modelimage.put("ready", ready);
			    
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		    Gson gson = new Gson();
			            
            return gson.toJson(modelimage);
        });
        
        post("/api/trainEktp2", (req, res) -> {
        	String imgName ="";
        	ArrayList<BufferedImage> imgsChar = new ArrayList<>();
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("trainedChar"));
			try {
				FileOutputStream fos = null;
				Part filePart=null;
				Gson gson = new GsonBuilder().create();
				ObjectDataInput [] odi = gson.fromJson(req.body(),ObjectDataInput[].class); 
				System.out.println(odi[2].charInput + ", " + odi[1].imgNum + ", " + odi[0].imgURL);

			    String imageString = odi[0].imgURL.substring("data:image/png;base64,"
			            .length());
			    byte[] decodedData = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageString);
			    String datetime = LocalDateTime.now().toString();
			    datetime = datetime.replace(":", ";");
			    imgName = "Character trained "+datetime+".jpg";
			    fos = new FileOutputStream("trainedChar\\"+imgName);
			    fos.write(decodedData);
			    fos.close();
			    CrossingsWeb cw = new CrossingsWeb();
			    Train tr = new Train();
			    File file = new File("trainedChar\\"+imgName);
			    cw.prosesCrossings(file, odi[2].charInput, odi[1].imgNum, tr.numImg);
   
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return "Oke";
        });
    }
    
    
}

