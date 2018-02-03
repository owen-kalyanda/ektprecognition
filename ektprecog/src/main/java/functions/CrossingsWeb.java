package functions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import dbEKTP.Database;

public class CrossingsWeb {

	public int getRed(int pixel){
		int red = (pixel >> 16) & 0xff;
		return red;
	}
	
	public void prosesCrossings(File file, String charToDB, int numImg, int numImgForRow1 ) throws IOException{
		BufferedImage characterImg = ImageIO.read(file);
		BufferedImage padImage2;
		int w = characterImg.getWidth();
		int h = characterImg.getHeight();
		BufferedImage padImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		if(w % 2 != 0){
			padImage = new BufferedImage(padImage.getWidth() + 1, padImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		if(h % 2 != 0){
			padImage = new BufferedImage(padImage.getWidth(), padImage.getHeight() + 1, BufferedImage.TYPE_INT_ARGB);
	    }
		
		padImage2 = new BufferedImage(padImage.getWidth() + 2, padImage.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = padImage2.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, padImage2.getWidth(), padImage2.getHeight());
		g.drawImage(characterImg, 1, 1, null);
		g.dispose();
		
		int colorpixel;
		int pixelAfter;
		int pixelBefore = 0;
		double count = 0;
		int count2 = 0;
		ArrayList<Double> horizontal = new ArrayList<>();
		ArrayList<Double> horizontal2 = new ArrayList<>();
		
		System.out.println("Width(vertikal): " + padImage2.getWidth() + ", Height(horizontal) :" + padImage2.getHeight());
		for(int row = 0; row < padImage2.getHeight(); row++ ){
			for (int col = 0; col < padImage2.getWidth(); col++) {
				colorpixel = padImage2.getRGB(col, row);
				if(row == 0 && col == 0){                          //scan pixel pertama
					pixelAfter = getRed(colorpixel);
					pixelBefore = pixelAfter;
					continue;
				}
				pixelAfter = getRed(colorpixel);
				if(pixelAfter == 0 && pixelBefore == 255){
					count++;
				}
				pixelBefore = pixelAfter;
			}
			count2++;
			if(count2 == 2){
				horizontal.add(count);
				count = 0;
				count2 = 0;
			}
		}
		
	    double hMax = Collections.max(horizontal);
	    System.out.println("Hmax :" + hMax);
	    System.out.print("horizontal : {");
	    for(int i = 0; i < horizontal.size(); i++){
	    	double hIndex = horizontal.get(i) / hMax;
	    	horizontal2.add(hIndex);
	    	if(i == horizontal.size()-1){
	    		System.out.println(hIndex + "}");
	    	}
	    	else{
	    	System.out.print(hIndex + " , ");
	    	}
	    }
	    
	    ArrayList<Double> vertikal = new ArrayList<>();
		ArrayList<Double> vertikal2 = new ArrayList<>();
		
	    for(int col = 0; col < padImage2.getWidth(); col++ ){
			for (int row = 0; row < padImage2.getHeight(); row++) {
				colorpixel = padImage2.getRGB(col, row);
				if(row == 0 && col == 0){                          //scan pixel pertama
					pixelAfter = getRed(colorpixel);
					pixelBefore = pixelAfter;
					continue;
				}
				pixelAfter = getRed(colorpixel);
				if(pixelAfter == 0 && pixelBefore == 255){
					count++;
				}
				pixelBefore = pixelAfter;
				
			}
			count2++;
			if(count2 == 2){
				vertikal.add(count);
				count = 0;
				count2 = 0;
			}
		}
	    
	    double vMax = Collections.max(vertikal);
	    System.out.println("Vmax :" + vMax);
	    System.out.print("vertikal : {");
	    for(int i = 0; i < vertikal.size(); i++){
	    	double vIndex = vertikal.get(i) / vMax;
	    	vertikal2.add(vIndex);
	    	if(i == vertikal.size()-1){
	    		System.out.println(vIndex + "}");
	    	}
	    	else{
	    	System.out.print(vIndex + " , ");
	    	}
	    } 

	    Database db = new Database();
	    
	    if (numImg < numImgForRow1) {
	    	try {
		    	db.insertData(horizontal2,vertikal2, charToDB, "tb_karakternikktpasli");
			} catch (Exception e) {
				System.out.println("data tidak masuk");
			}
		}
	    else {
	    	try {
		    	db.insertData(horizontal2,vertikal2, charToDB, "tb_karakterktpasli");
			} catch (Exception e) {
				System.out.println("data tidak masuk");
			}
	    }
	    
	    horizontal.clear();
	    horizontal2.clear();
	    vertikal.clear();
	    vertikal2.clear();
	}
}
