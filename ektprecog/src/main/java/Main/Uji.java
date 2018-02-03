package Main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.border.TitledBorder;


import dbEKTP.Database;
import dbEKTP.ObjectFeatureCrossings;
import functions.BufferedImageSharpen;
import functions.CCL;
import functions.Crossings;
import functions.GaussianFilter;

import javax.swing.UIManager;
import javax.swing.SwingConstants;

public class Uji extends JFrame {
	int pushedCrop = 0;
	int pushedOpen = 0;
	int countCross = 0;
	int countVert = 0;
	File file = null;
	BufferedImage newImg;
	BufferedImage newNewImg;
	BufferedImage croppedImg;
	BufferedImage newCroppedImg;
	BufferedImage newCroppedImg2;
	BufferedImage img;
	static BufferedImage newBWImg;
	BufferedImage newGreyscalledImg;
	int panjangBaris;
	int batasAtas;
	int batasBawah;
	int noVImg = 0;
	ArrayList<Integer> arrayPixelHorizontal = new ArrayList<>();
	ArrayList<Integer> arrayPixelVertikal = new ArrayList<>();
	ArrayList<Integer> arrayPixelVertikalPutih = new ArrayList<>();
	ArrayList<Integer> arrayNilaiRGB = new ArrayList<>();
	ArrayList<Double> arrayKernel = new ArrayList<>();
	ArrayList<Integer> arrayPixelPadded = new ArrayList<>();
	ArrayList<Integer> arrayY = new ArrayList<>();
	ArrayList<BufferedImage> croppedHImg= new ArrayList<>();
	ArrayList<BufferedImage> croppedVImg= new ArrayList<>();
	ArrayList<Double> horizontalUji = new ArrayList<>();
	ArrayList<Double> horizontalUji2 = new ArrayList<>();
    ArrayList<Double> vertikalUji = new ArrayList<>();
	ArrayList<Double> vertikalUji2 = new ArrayList<>();
	ArrayList<Integer> arrSizeH = new ArrayList<>();
	ArrayList<Integer> arrSizeV = new ArrayList<>();
	ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> tempArrH = new ArrayList<>();
	ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> tempArrV = new ArrayList<>();
	PrintStream out;
	JFrame frame2;
	JFrame frame = new JFrame("Cropped Horizontal");
	JFrame frame3 = new JFrame("Hasil crop vertikal");
	private JPanel contentPane;
	private JTextField tfNilaiThreshold;
	private JTextField tfMCX;
	private JTextField tfMCY;
	private JTextField tfWidthCrop;
	private JTextField tfHeightCrop;
	JTextField tfCharacter;
	JTextField tfImgNum;
	JLabel lblCroppedKTP;
	JLabel lblFirstImage;
	JLabel lblChar;
	ArrayList<JLabel> lblHCropped = new ArrayList<>();
	ArrayList<JLabel> lblVCropped = new ArrayList<>();
	JButton btnCrossings = new JButton("Crossings");
	JButton btnUji = new JButton("Uji");
	public static int nomorImg = 0;
	static ArrayList<ArrayList<BufferedImage>> arrImgCropped = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Uji frame = new Uji();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	CCL ccl = new CCL();
	
	public int getRed(int pixel){
		int red = (pixel >> 16) & 0xff;
		return red;
	}

	public int getGreen(int pixel){
		int green = (pixel >> 8) & 0xff;
		return green;
	}
	
	public int getBlue(int pixel){
		int blue = (pixel) & 0xff;
		return blue;
	}

	public BufferedImage getNewImg() {
        return newImg;
    }
	
	private void cariPixelHorizontal() {
	    int w = newBWImg.getWidth();
	    int h = newBWImg.getHeight();
	    arrayPixelHorizontal = new ArrayList<>();
	    for (int i = 0; i < h; i++) {
	    	int jmlPixelHitam = 0;
	      for (int j = 0; j < w; j++) {
	        int pixel = newBWImg.getRGB(j, i);
	        int red = getRed(pixel);
	        if(red == 0){
	        	jmlPixelHitam++;
	        }

	      }
	      arrayPixelHorizontal.add(jmlPixelHitam);
	    }
	}

	private void drawLineHorizontal() {
		int w = newBWImg.getWidth();
		int h = newBWImg.getHeight();
		for(int i = 0; i < h; i++){
			
			try {
				if(arrayPixelHorizontal.get(i) != 0 && arrayPixelHorizontal.get(i+1) == 0 && arrayPixelHorizontal.get(i-1) != 0){
					Graphics g2 = newBWImg.getGraphics();
			        g2.setColor(Color.BLUE);
			        g2.drawLine(0, i+1, w, i+1);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Graphics g2 = newBWImg.getGraphics();
		        g2.setColor(Color.BLUE);
		        g2.drawLine(0, i+1, w, i+1);
			}
			
			}
		arrayPixelHorizontal = new ArrayList<>();

		}
	
	private void cropHorizontal() {
		frame.setSize(1024, 768);
		frame.getContentPane().setLayout(null);
		int w = newBWImg.getWidth();
		int h = newBWImg.getHeight();
		int h1 = 0;
		int h2;
		int h3;
		int a = 0;
		int b = 0;
		for(int i = 0; i < h; i++){
			int color = newBWImg.getRGB(0, i);
			int blue = getBlue(color);
			int red = getRed(color);
			int green = getGreen(color);
			if(blue == 255 && red == 0 && green == 0){
				h2 = i;
				h3 = h2 - h1;
				croppedHImg.add(newBWImg.getSubimage(0, h1 + 1, w, h3-1));
				lblHCropped.add(new JLabel());
				Border border = BorderFactory.createLineBorder(Color.BLUE, 1);
				lblHCropped.get(b).setBorder(border);
				lblHCropped.get(b).setBounds(10, 10 + a, w, h3);
				frame.getContentPane().add(lblHCropped.get(b));
				lblHCropped.get(b).setIcon(new ImageIcon(croppedHImg.get(b)));
				h1 = h2;
				a = a + 40;
				b++;
			}
	
		}
		frame.setVisible(true);
	}	
	
	private void cariPixelVertikal(BufferedImage croppedImgH) {
	    int w = croppedImgH.getWidth();
	    int h = croppedImgH.getHeight();
	    arrayPixelVertikal.clear();

	    for (int i = 0; i < w; i++) {
	    	int jmlPixelHitam = 0;
	      for (int j = 0; j < h; j++) {
	        int pixel = croppedImgH.getRGB(i, j);
	        int red = getRed(pixel);
	        if(red == 0){
	        	jmlPixelHitam++;
	        }

	      }
	      arrayPixelVertikal.add(jmlPixelHitam);
	    }
	}
	
	private void drawLineVertikal(BufferedImage croppedImgH, JLabel lblCroppedH) {
		int w = croppedImgH.getWidth();
		int h = croppedImgH.getHeight();

		if (countVert == 0) {
			for(int i = 0; i < w; i++){
				try {
					if(arrayPixelVertikal.get(i) != 0  && arrayPixelVertikal.get(i+1) == 0 && arrayPixelVertikal.get(i+2) == 0 && arrayPixelVertikal.get(i+3) == 0 && arrayPixelVertikal.get(i+4) == 0 && arrayPixelVertikal.get(i+5) == 0 && arrayPixelVertikal.get(i+6) == 0 && arrayPixelVertikal.get(i+7) == 0 && arrayPixelVertikal.get(i+8) == 0 && arrayPixelVertikal.get(i+9) == 0 && arrayPixelVertikal.get(i+10) == 0 && arrayPixelVertikal.get(i-1) != 0){
						Graphics g2 = croppedImgH.getGraphics();
				        g2.setColor(Color.BLUE);
				        g2.drawLine(i+2, 0, i+2, h);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Graphics g2 = croppedImgH.getGraphics();
			        g2.setColor(Color.BLUE);
			        g2.drawLine(i+2, 0, i+2, h);
				}
				
			}
		}
		else{
			for(int i = 0; i < w; i++){
				try {
					if(arrayPixelVertikal.get(i) != 0  && arrayPixelVertikal.get(i+1) == 0 && arrayPixelVertikal.get(i+2) == 0 && arrayPixelVertikal.get(i+3) == 0 && arrayPixelVertikal.get(i+4) == 0 && arrayPixelVertikal.get(i+5) == 0 && arrayPixelVertikal.get(i+6) == 0 && arrayPixelVertikal.get(i-1) != 0){
						Graphics g2 = croppedImgH.getGraphics();
				        g2.setColor(Color.BLUE);
				        g2.drawLine(i+2, 0, i+2, h);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Graphics g2 = croppedImgH.getGraphics();
			        g2.setColor(Color.BLUE);
			        g2.drawLine(i+2, 0, i+2, h);
				}
				
			}
		}
		lblCroppedH.setIcon(new ImageIcon(croppedImgH));
		countVert++;
		
		}
	
	private void cropVertikal(BufferedImage croppedImgH, JFrame frame, int y2) {
		int w = croppedImgH.getWidth();
		int h = croppedImgH.getHeight();
		int h1 = 0;
		int h2;
		int h3;
		int a = 0;
		for(int i = 0; i < w; i++){
			int color = croppedImgH.getRGB(i, 0);
			int blue = getBlue(color);
			int red = getRed(color);
			int green = getGreen(color);
			if(blue == 255 && red == 0 && green == 0){
				h2 = i;
				h3 = h2 - h1;
				croppedVImg.add(croppedImgH.getSubimage(h1 + 1, 0, h3, h));
				JLabel lblVCropped = new JLabel();
				Border border = BorderFactory.createLineBorder(Color.BLUE, 1);
				lblVCropped.setBorder(border);
				lblVCropped.setBounds(10 + a, y2, croppedVImg.get(noVImg).getWidth(), croppedVImg.get(noVImg).getHeight());
				frame.getContentPane().add(lblVCropped);
				lblVCropped.setIcon(new ImageIcon(croppedVImg.get(noVImg)));
				a = a + croppedVImg.get(noVImg).getWidth() + 10;
				noVImg++;
				h1 = h2;
			}
	
		}
	}
	
	private void crossingsUji(BufferedImage characterImg){
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

		System.out.println("Width(vertikal): " + padImage2.getWidth() + ", Height(horizontal) :" + padImage2.getHeight());
		for(int row = 0; row < padImage2.getHeight(); row++ ){
			for (int col = 0; col < padImage2.getWidth(); col++) {
				colorpixel = padImage2.getRGB(col, row);
				if(row == 0 && col == 0){                
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
				horizontalUji.add(count);
				count = 0;
				count2 = 0;
			}
		}
		
	    double hMax = Collections.max(horizontalUji);
	    System.out.println("Hmax :" + hMax);
	    System.out.print("horizontal : {");
	    for(int i = 0; i < horizontalUji.size(); i++){
	    	double hIndex = horizontalUji.get(i) / hMax;
	    	horizontalUji2.add(hIndex);
	    	if(i == horizontalUji.size()-1){
	    		System.out.println(hIndex + "}");
	    	}
	    	else{
	    	System.out.print(hIndex + " , ");
	    	}
	    }

	    for(int col = 0; col < padImage2.getWidth(); col++ ){
			for (int row = 0; row < padImage2.getHeight(); row++) {
				colorpixel = padImage2.getRGB(col, row);
				if(row == 0 && col == 0){             
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
				vertikalUji.add(count);
				count = 0;
				count2 = 0;
			}
		}
	    
	    double vMax = Collections.max(vertikalUji);
	    System.out.println("Vmax :" + vMax);
	    System.out.print("vertikal : {");
	    for(int j = 0; j < vertikalUji.size(); j++){
	    	double vIndex = vertikalUji.get(j) / vMax;
	    	vertikalUji2.add(vIndex);
	    	if(j == vertikalUji.size()-1){
	    		System.out.println(vIndex + "}");
	    	}
	    	else{
	    	System.out.print(vIndex + " , ");
	    	}
	    } 
	}
	
	private void CCL_Crossings() throws IOException{
		File index = new File("charactersUji");
		index.mkdir();
		frame2 = new JFrame("Hasil CCL");
		frame2.setSize(1240, 768);
		frame2.setLayout(null);
		
		
		frame3.setSize(1024, 768);
		frame3.setLayout(null);
		
		int y = 0;
		int temp = 0;
		int tempV = 0;
		int y2 = 10;
		noVImg = 0;
		countVert = 0;
		
		ArrayList<ArrayList<Double>> arrHSementara = new ArrayList<>();
		ArrayList<ArrayList<Double>> arrVSementara = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<Double>>> arrHSementara2 = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<Double>>> arrVSementara2 = new ArrayList<>();

		
		for (int i = 0; i < croppedHImg.size(); i++) {
			cariPixelVertikal(croppedHImg.get(i));
			drawLineVertikal(croppedHImg.get(i), lblHCropped.get(i));
			cropVertikal(croppedHImg.get(i), frame3, y2);
			y2 = y2 + 40;
			arrayPixelVertikal = new ArrayList<>();
			for (int j = tempV; j < croppedVImg.size(); j++) {
				ccl.prosesCCL(croppedVImg.get(j), frame2, y);
				for (int k = temp; k < ccl.charImg.size(); k++) {
					ImageIO.write(ccl.charImg.get(k), "png", new File("charactersUji\\charcomponent-" + k + ".png"));
					crossingsUji(ccl.charImg.get(k));
					arrHSementara.add(horizontalUji2); 
					arrVSementara.add(vertikalUji2);
					arrSizeH.add(horizontalUji2.size()); 
					arrSizeV.add(vertikalUji2.size());
					horizontalUji = new ArrayList<>();
					horizontalUji2 = new ArrayList<>();
					vertikalUji = new ArrayList<>();
					vertikalUji2 = new ArrayList<>();
				}
				arrHSementara2.add(arrHSementara);
				arrVSementara2.add(arrVSementara);
				arrHSementara = new ArrayList<>();
				arrVSementara = new ArrayList<>();
				temp=ccl.charImg.size();
			}
			ccl.a = 0;
			tempArrH.add(arrHSementara2); 
			tempArrV.add(arrVSementara2);
			arrHSementara2 = new ArrayList<>();
			arrVSementara2 = new ArrayList<>();
			tempV = croppedVImg.size();
			y = y + 48;
		}
		frame3.setVisible(true);
		ccl.charImg = new ArrayList<>();
		ccl.b = 0;
		horizontalUji = new ArrayList<>();
		horizontalUji2 = new ArrayList<>();
		vertikalUji = new ArrayList<>();
		vertikalUji2 = new ArrayList<>();
		arrHSementara = new ArrayList<>();
		arrVSementara = new ArrayList<>();
		croppedHImg = new ArrayList<>();
		croppedVImg = new ArrayList<>();
		tfImgNum = new JTextField();
		tfCharacter = new JTextField();
		tfImgNum.setBounds(30, 650, 30, 30);
		tfCharacter.setBounds(70, 650, 30, 30);
		btnCrossings.setBounds(110, 650, 100, 50);
		btnUji.setBounds(220, 650, 100, 50);
		frame2.add(tfImgNum);
		frame2.add(tfCharacter);
		frame2.add(btnCrossings);
		frame2.add(btnUji);
		frame2.setVisible(true);
	}

	
private ArrayList<ArrayList<String>> ED() throws ClassNotFoundException, SQLException, IOException{
		
		//mengambil data dari db
		ArrayList<String> infoKtpFinal = new ArrayList<>();
		
		ArrayList<ArrayList<Double>> featureHNik = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureVNik = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureH = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureV = new ArrayList<>();
		ArrayList<String> charDigitalNik = new ArrayList<>();
		ArrayList<String> charDigital = new ArrayList<>();
		
		ArrayList<ArrayList<Double>> featureHNikWord = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureVNikWord = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureHWord = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureVWord = new ArrayList<>();
		ArrayList<String> charDigitalNikWord = new ArrayList<>();
		ArrayList<String> charDigitalWord = new ArrayList<>();
		
		Database db = new Database();
		
		ArrayList<Integer> idNikKtpAsli = db.getId(db.getConnection(), "tb_karakternikktpasli");
		ArrayList<Integer> idKtpAsli = db.getId(db.getConnection(), "tb_karakterktpasli");
		
		ArrayList<Integer> idNikWord = db.getId(db.getConnection(), "tb_karakternikwordscan");
		ArrayList<Integer> idWord = db.getId(db.getConnection(), "tb_karakterwordscan");
		

		for (int i : idNikKtpAsli) {
			db.selectData(i, "tb_karakternikktpasli");
			FileInputStream fileIn = new FileInputStream("Out.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ObjectFeatureCrossings of = new ObjectFeatureCrossings();
			of= (ObjectFeatureCrossings)in.readObject();
			in.close();
			fileIn.close();
			charDigitalNik.add(of.karakter);
			ArrayList<Double> dataH = of.data;
			arrSizeH.add(dataH.size());
			featureHNik.add(dataH);
			ArrayList<Double> dataV = of.data2;
			arrSizeV.add(dataV.size());
			featureVNik.add(dataV);
			dataH = new ArrayList<>();
			dataV = new ArrayList<>();
		}
		
		for (int i : idKtpAsli) {
			db.selectData(i, "tb_karakterktpasli");
			FileInputStream fileIn = new FileInputStream("Out.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ObjectFeatureCrossings of = new ObjectFeatureCrossings();
			of= (ObjectFeatureCrossings)in.readObject();
			in.close();
			fileIn.close();
			charDigital.add(of.karakter);
			ArrayList<Double> dataH = of.data;
			arrSizeH.add(dataH.size());
			featureH.add(dataH);
			ArrayList<Double> dataV = of.data2;
			arrSizeV.add(dataV.size());
			featureV.add(dataV);
			dataH = new ArrayList<>();
			dataV = new ArrayList<>();
		}

		for (int i : idNikWord) {
			db.selectData(i, "tb_karakternikwordscan");
			FileInputStream fileIn = new FileInputStream("Out.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ObjectFeatureCrossings of = new ObjectFeatureCrossings();
			of= (ObjectFeatureCrossings)in.readObject();
			in.close();
			fileIn.close();
			charDigitalNikWord.add(of.karakter);
			ArrayList<Double> dataH = of.data;
			arrSizeH.add(dataH.size());
			featureHNikWord.add(dataH);
			ArrayList<Double> dataV = of.data2;
			arrSizeV.add(dataV.size());
			featureVNikWord.add(dataV);
			dataH = new ArrayList<>();
			dataV = new ArrayList<>();
		}
		
		for (int i : idWord) {
			db.selectData(i, "tb_karakterwordscan");
			FileInputStream fileIn = new FileInputStream("Out.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ObjectFeatureCrossings of = new ObjectFeatureCrossings();
			of= (ObjectFeatureCrossings)in.readObject();
			in.close();
			fileIn.close();
			charDigitalWord.add(of.karakter);
			ArrayList<Double> dataH = of.data;
			arrSizeH.add(dataH.size());
			featureHWord.add(dataH);
			ArrayList<Double> dataV = of.data2;
			arrSizeV.add(dataV.size());
			featureVWord.add(dataV);
			dataH = new ArrayList<>();
			dataV = new ArrayList<>();
		}
		
		//menyamakan ukuran array
		int maxSizeH = Collections.max(arrSizeH);
		int maxSizeV = Collections.max(arrSizeV);

		System.out.println("Size pertama: " + tempArrH.get(1).get(0).size());
		System.out.println("Size terbesar: " +maxSizeH);
		
		for (int i = 0; i < tempArrH.size(); i++) {
			for (int j = 0; j < tempArrH.get(i).size(); j++) {
				for (int l = 0; l < tempArrH.get(i).get(j).size(); l++) {
					if (tempArrH.get(i).get(j).get(l).size() < maxSizeH) {
						int tmbIndex = maxSizeH - tempArrH.get(i).get(j).get(l).size();
						for (int k = 0; k < tmbIndex; k++) {
							tempArrH.get(i).get(j).get(l).add(0.);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < tempArrV.size(); i++) {
			for (int j = 0; j < tempArrV.get(i).size(); j++) {
				for (int l = 0; l < tempArrV.get(i).get(j).size(); l++) {
					if (tempArrV.get(i).get(j).size() < maxSizeV) {
						int tmbIndex = maxSizeV - tempArrV.get(i).get(j).get(l).size();
						for (int k = 0; k < tmbIndex; k++) {
							tempArrV.get(i).get(j).get(l).add(0.);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < featureHNik.size(); i++) {	
			if (featureHNik.get(i).size() < maxSizeH) {
				int tmbIndex = maxSizeH - featureHNik.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureHNik.get(i).add(0.);
				}
			}
		}

		for (int i = 0; i < featureVNik.size(); i++) {	
			if (featureVNik.get(i).size() < maxSizeV) {
				int tmbIndex = maxSizeV - featureVNik.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureVNik.get(i).add(0.);
				}
			}
		}
		
		for (int i = 0; i < featureH.size(); i++) {	
			if (featureH.get(i).size() < maxSizeH) {
				int tmbIndex = maxSizeH - featureH.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureH.get(i).add(0.);
				}
			}
		}

		for (int i = 0; i < featureV.size(); i++) {	
			if (featureV.get(i).size() < maxSizeV) {
				int tmbIndex = maxSizeV - featureV.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureV.get(i).add(0.);
				}
			}
		}
		
		for (int i = 0; i < featureHNikWord.size(); i++) {	
			if (featureHNikWord.get(i).size() < maxSizeH) {
				int tmbIndex = maxSizeH - featureHNikWord.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureHNikWord.get(i).add(0.);
				}
			}
		}

		for (int i = 0; i < featureVNikWord.size(); i++) {	
			if (featureVNikWord.get(i).size() < maxSizeV) {
				int tmbIndex = maxSizeV - featureVNikWord.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureVNikWord.get(i).add(0.);
				}
			}
		}
		
		for (int i = 0; i < featureHWord.size(); i++) {	
			if (featureHWord.get(i).size() < maxSizeH) {
				int tmbIndex = maxSizeH - featureHWord.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureHWord.get(i).add(0.);
				}
			}
		}

		for (int i = 0; i < featureVWord.size(); i++) {	
			if (featureVWord.get(i).size() < maxSizeV) {
				int tmbIndex = maxSizeV - featureVWord.get(i).size();
				for (int k = 0; k < tmbIndex; k++) {
					featureVWord.get(i).add(0.);
				}
			}
		}
		
		//penggabungan array
		ArrayList<Double> tempcombined = new ArrayList<>();
		ArrayList<ArrayList<Double>> temp2combined = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<Double>>> featureCombined = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> featureCombinedFinal = new ArrayList<>();
		
		for (int i = 0; i < tempArrH.size(); i++) {
			for (int j = 0; j < tempArrH.get(i).size(); j++) {
				for (int k = 0; k < tempArrH.get(i).get(j).size(); k++) {
					for (int l = 0; l < tempArrH.get(i).get(j).get(k).size(); l++) {
						tempcombined.add(tempArrH.get(i).get(j).get(k).get(l));
					}
					for (int m = 0; m < tempArrV.get(i).get(j).get(k).size(); m++) {
						tempcombined.add(tempArrV.get(i).get(j).get(k).get(m));
					}
					temp2combined.add(tempcombined);
					tempcombined = new ArrayList<>();
				}
				featureCombined.add(temp2combined);
				temp2combined = new ArrayList<>();	
			}
			featureCombinedFinal.add(featureCombined);
			featureCombined = new ArrayList<>();
		}
		
		ArrayList<Double> tempcombineddbnik = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureCombinedDBNik = new ArrayList<>();
		
		for (int i = 0; i < featureHNik.size(); i++) {
			for (int j = 0; j < featureHNik.get(i).size(); j++) {
				tempcombineddbnik.add(featureHNik.get(i).get(j));
			}
			for (int j = 0; j < featureVNik.get(i).size(); j++) {
				tempcombineddbnik.add(featureVNik.get(i).get(j));
			}
			featureCombinedDBNik.add(tempcombineddbnik);
			tempcombineddbnik = new ArrayList<>();
		}
		
		
		ArrayList<Double> tempcombineddb = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureCombinedDB = new ArrayList<>();
		
		for (int i = 0; i < featureH.size(); i++) {
			for (int j = 0; j < featureH.get(i).size(); j++) {
				tempcombineddb.add(featureH.get(i).get(j));
			}
			for (int j = 0; j < featureV.get(i).size(); j++) {
				tempcombineddb.add(featureV.get(i).get(j));
			}
			featureCombinedDB.add(tempcombineddb);
			tempcombineddb = new ArrayList<>();
		}
		
		ArrayList<Double> tempcombineddbnikword = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureCombinedDBNikword = new ArrayList<>();
		
		for (int i = 0; i < featureHNikWord.size(); i++) {
			for (int j = 0; j < featureHNikWord.get(i).size(); j++) {
				tempcombineddbnikword.add(featureHNikWord.get(i).get(j));
			}
			for (int j = 0; j < featureVNikWord.get(i).size(); j++) {
				tempcombineddbnikword.add(featureVNikWord.get(i).get(j));
			}
			featureCombinedDBNikword.add(tempcombineddbnikword);
			tempcombineddbnikword = new ArrayList<>();
		}
		
		
		ArrayList<Double> tempcombineddbword = new ArrayList<>();
		ArrayList<ArrayList<Double>> featureCombinedDBword = new ArrayList<>();
		
		for (int i = 0; i < featureHWord.size(); i++) {
			for (int j = 0; j < featureHWord.get(i).size(); j++) {
				tempcombineddbword.add(featureHWord.get(i).get(j));
			}
			for (int j = 0; j < featureVWord.get(i).size(); j++) {
				tempcombineddbword.add(featureVWord.get(i).get(j));
			}
			featureCombinedDBword.add(tempcombineddbword);
			tempcombineddbword = new ArrayList<>();
		}
		
		//euclidean distance
		ArrayList<Double> arrEuclid = new ArrayList<>();
		ArrayList<String> infoKTPDigital = new ArrayList<>();
		ArrayList<String> infoKTPDigitalword = new ArrayList<>();
		ArrayList<String> infoJoined1 = new ArrayList<>();
		ArrayList<String> infoJoined1word = new ArrayList<>();
		ArrayList<String> infoJoinedCetak = new ArrayList<>();
		ArrayList<String> infoJoinedCetakword = new ArrayList<>();
		ArrayList<String> infoJoinedFinal = new ArrayList<>();
		ArrayList<String> infoJoinedFinalword = new ArrayList<>();
		
		for (int i = 0; i < featureCombinedFinal.size(); i++) {
			for (int j = 0; j < featureCombinedFinal.get(i).size(); j++) {
				for (int m = 0; m < featureCombinedFinal.get(i).get(j).size(); m++) {
					if (i == 0) { 
						for (int k = 0; k < featureCombinedDBNik.size(); k++) {
							double euclid = 0;
							for (int l = 0; l < featureCombinedFinal.get(i).get(j).get(m).size(); l++) {
								euclid = euclid + Math.pow((featureCombinedDBNik.get(k).get(l) - featureCombinedFinal.get(i).get(j).get(m).get(l)), 2);
							}
							euclid = Math.sqrt(euclid);
							arrEuclid.add(euclid);
						}
						double min = arrEuclid.get(0);
						int minIndex = 0;
						for(int n = 0; n < arrEuclid.size(); n++) {
						    double number = arrEuclid.get(n);
						    if(number < min) {
						    	min = number;
						    	minIndex = n;
						    }
						}
						infoKTPDigital.add(charDigitalNik.get(minIndex));
						arrEuclid = new ArrayList<>();
						
						for (int k = 0; k < featureCombinedDBNikword.size(); k++) {
							double euclid = 0;
							for (int l = 0; l < featureCombinedFinal.get(i).get(j).get(m).size(); l++) {
								euclid = euclid + Math.pow((featureCombinedDBNikword.get(k).get(l) - featureCombinedFinal.get(i).get(j).get(m).get(l)), 2);
							}
							euclid = Math.sqrt(euclid);
							arrEuclid.add(euclid);
						}
						double min2 = arrEuclid.get(0);
						int minIndex2 = 0;
						for(int n = 0; n < arrEuclid.size(); n++) {
						    double number = arrEuclid.get(n);
						    if(number < min2) {
						    	min2 = number;
						    	minIndex2 = n;
						    }
						}
						infoKTPDigitalword.add(charDigitalNikWord.get(minIndex2));
						arrEuclid = new ArrayList<>();
					}
					else{
						for (int k = 0; k < featureCombinedDB.size(); k++) {
							double euclid = 0;
							for (int l = 0; l < featureCombinedFinal.get(i).get(j).get(m).size(); l++) {
								euclid = euclid + Math.pow((featureCombinedDB.get(k).get(l) - featureCombinedFinal.get(i).get(j).get(m).get(l)), 2);
							}
							euclid = Math.sqrt(euclid);
							arrEuclid.add(euclid);
						}
						double min = arrEuclid.get(0);
						int minIndex = 0;
						for(int n = 0; n < arrEuclid.size(); n++) {
						    double number = arrEuclid.get(n);
						    if(number < min) {
						    	min = number;
						    	minIndex = n;
						    }
						}
						infoKTPDigital.add(charDigital.get(minIndex));
						arrEuclid = new ArrayList<>();
						
						for (int k = 0; k < featureCombinedDBword.size(); k++) {
							double euclid = 0;
							for (int l = 0; l < featureCombinedFinal.get(i).get(j).get(m).size(); l++) {
								euclid = euclid + Math.pow((featureCombinedDBword.get(k).get(l) - featureCombinedFinal.get(i).get(j).get(m).get(l)), 2);
							}
							euclid = Math.sqrt(euclid);
							arrEuclid.add(euclid);
						}
						double min2 = arrEuclid.get(0);
						int minIndex2 = 0;
						for(int n = 0; n < arrEuclid.size(); n++) {
						    double number = arrEuclid.get(n);
						    if(number < min2) {
						    	min2 = number;
						    	minIndex2 = n;
						    }
						}
						infoKTPDigitalword.add(charDigitalWord.get(minIndex2));
						arrEuclid = new ArrayList<>();
					}
					
				}

				infoJoined1.add(String.join("", infoKTPDigital));
				infoKTPDigital = new ArrayList<>();
				infoJoined1word.add(String.join("", infoKTPDigitalword));
				infoKTPDigitalword = new ArrayList<>();
			}
			infoJoinedFinal.add(String.join(" ", infoJoined1));
			infoJoinedCetak.add(String.join("", infoJoined1));
			infoJoined1 = new ArrayList<>();
			infoJoinedFinalword.add(String.join(" ", infoJoined1word));
			infoJoinedCetakword.add(String.join("", infoJoined1word));
			infoJoined1word = new ArrayList<>();
		}
		
		String cetak = String.join("", infoJoinedCetak);
		String cetakword = String.join("", infoJoinedCetakword);
		System.out.println(cetak);
		System.out.println(cetakword);
		
		tempArrH = new ArrayList<>();
		tempArrV = new ArrayList<>();
		
		ArrayList<ArrayList<String>> infoEKTP = new ArrayList<>();
		infoEKTP.add(infoJoinedFinal);
		infoEKTP.add(infoJoinedFinalword);
		return infoEKTP;
	}
	
	/**
	 * Create the frame.
	 */
	public Uji() {
		setTitle("Pengujian");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1300, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnOpenFile = new JButton("Open File");
		btnOpenFile.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnOpenFile.setBounds(15, 519, 158, 40);
		contentPane.add(btnOpenFile);
		
		JButton btnManualCrop = new JButton("Manual Crop");
		btnManualCrop.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnManualCrop.setBounds(223, 603, 158, 40);
		contentPane.add(btnManualCrop);
		
		JButton btnGaussian = new JButton("<html>Gaussian Filter<br />& Sharpening </html> ");
		btnGaussian.setName("");
		btnGaussian.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnGaussian.setBounds(15, 593, 158, 50);
		contentPane.add(btnGaussian);
		
		JButton btnThresholding2 = new JButton("Thresholding");
		btnThresholding2.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnThresholding2.setBounds(429, 603, 158, 40);
		contentPane.add(btnThresholding2);
		
		tfNilaiThreshold = new JTextField();
		tfNilaiThreshold.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfNilaiThreshold.setText("105");
		tfNilaiThreshold.setBounds(429, 547, 70, 30);
		contentPane.add(tfNilaiThreshold);
		tfNilaiThreshold.setColumns(10);
		
		JLabel lblNilaiThreshold = new JLabel("Nilai Threshold");
		lblNilaiThreshold.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNilaiThreshold.setBounds(429, 500, 158, 30);
		contentPane.add(lblNilaiThreshold);
		
		tfMCX = new JTextField();
		tfMCX.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfMCX.setText("25");
		tfMCX.setBounds(252, 525, 38, 30);
		contentPane.add(tfMCX);
		tfMCX.setColumns(10);
		
		tfMCY = new JTextField();
		tfMCY.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfMCY.setText("60");
		tfMCY.setColumns(10);
		tfMCY.setBounds(252, 562, 38, 30);
		contentPane.add(tfMCY);
		
		JLabel lblX = new JLabel("x :");
		lblX.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblX.setBounds(218, 525, 48, 27);
		contentPane.add(lblX);
		
		JLabel lblY = new JLabel("y :");
		lblY.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblY.setBounds(218, 564, 38, 23);
		contentPane.add(lblY);
		
		JLabel lblPengaturanManualCropping = new JLabel("Pengaturan Manual Cropping");
		lblPengaturanManualCropping.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPengaturanManualCropping.setBounds(218, 489, 201, 25);
		contentPane.add(lblPengaturanManualCropping);
		
		JLabel lblCropped2KTP = new JLabel("");
		lblCropped2KTP.setBounds(731, 422, 50, 50);
		contentPane.add(lblCropped2KTP);
		
		JPanel panel = new JPanel();
		panel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Gambar Awal", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Gambar Awal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(5, 10, 710, 435);
		contentPane.add(panel);
		
		
		
		
		
		tfWidthCrop = new JTextField();
		tfWidthCrop.setText("500");
		tfWidthCrop.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfWidthCrop.setColumns(10);
		tfWidthCrop.setBounds(358, 525, 38, 30);
		contentPane.add(tfWidthCrop);
		
		tfHeightCrop = new JTextField();
		tfHeightCrop.setText("300");
		tfHeightCrop.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfHeightCrop.setColumns(10);
		tfHeightCrop.setBounds(358, 562, 38, 30);
		contentPane.add(tfHeightCrop);
		
		JLabel lblWidth = new JLabel("Width :");
		lblWidth.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblWidth.setBounds(300, 526, 48, 27);
		contentPane.add(lblWidth);
		
		JLabel lblHeight = new JLabel("Height :");
		lblHeight.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblHeight.setBounds(300, 565, 63, 23);
		contentPane.add(lblHeight);
		
		JButton btnCropHorizontal = new JButton("Crop Horizontal");
		
		btnCropHorizontal.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCropHorizontal.setBounds(637, 603, 158, 40);
		contentPane.add(btnCropHorizontal);
		
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pushedOpen > 0){
					lblFirstImage.setIcon(null);
					ccl.charImg = new ArrayList<>();
				}
			
				try {
					JFileChooser jfc = new JFileChooser(); 
					if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
						file = jfc.getSelectedFile() ; 
			        }
					else{
						return;
					}
					img = ImageIO.read(file);
					int imgW = img.getWidth();
					int imgH = img.getHeight();
					lblFirstImage = new JLabel("");
					lblFirstImage.setBounds(0, 20, imgW, imgH);
					panel.add(lblFirstImage);
					lblFirstImage.setIcon(new ImageIcon(img));
				} catch (IOException e1) {
					System.out.println("Input gagal dimasukkan");
				}
				pushedOpen++;
			}
		});
		
		btnGaussian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GaussianFilter gf = new GaussianFilter();
				gf.prosesGaussian(img);
	
			    BufferedImageSharpen bis = new BufferedImageSharpen();
				img = bis.process(img);
				lblFirstImage.setIcon(new ImageIcon(img));
			}
		});
		
		btnManualCrop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pushedCrop > 0){
					lblCroppedKTP.setIcon(null);
				}
				
				int y = Integer.parseInt(tfMCY.getText());
				int w = Integer.parseInt(tfWidthCrop.getText());
				int h = img.getHeight();
				croppedImg = img.getSubimage(4, y, w, h-y);
				lblCroppedKTP = new JLabel("");
				lblCroppedKTP.setBounds(720, 22, w, h);
				contentPane.add(lblCroppedKTP);
				lblCroppedKTP.setIcon(new ImageIcon(croppedImg));
				pushedCrop++;
			}
		});
		
		btnThresholding2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int lblW = croppedImg.getWidth();
				int lblH = croppedImg.getHeight();
				  BufferedImage newGreyscalledImg = new BufferedImage(lblW, lblH, BufferedImage.TYPE_BYTE_GRAY);
		    	  Graphics g = newGreyscalledImg.createGraphics();
		    	  g.drawImage(croppedImg, 0, 0, lblW, lblH, null);
		    	  g.dispose();
	
				  int w = newGreyscalledImg.getWidth();
				  int h = newGreyscalledImg.getHeight();
		
				  int nilaiThreshold = Integer.parseInt(tfNilaiThreshold.getText());
				  for (int i = 0; i < h; i++) {
				      for (int j = 0; j < w; j++) {
						  Color c = new Color(newGreyscalledImg.getRGB(j, i));
					      if (c.getRed() < nilaiThreshold && c.getGreen() < nilaiThreshold && c.getBlue() < nilaiThreshold) {
					    	  newGreyscalledImg.setRGB(j, i, Color.black.getRGB());
					      }
					      else
					      {
					    	  newGreyscalledImg.setRGB(j, i, Color.white.getRGB());
					      }
				      }
				  }
				  newBWImg = new BufferedImage(lblW, lblH, BufferedImage.TYPE_INT_ARGB);
		    	  Graphics g1 = newBWImg.createGraphics();
		    	  g1.drawImage(newGreyscalledImg, 0, 0, lblW, lblH, null);
		    	  g1.dispose();
		    	  lblCroppedKTP.setIcon(new ImageIcon(newBWImg));
		    	  
			}
		});
		
		btnCropHorizontal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cariPixelHorizontal();
				drawLineHorizontal();
				cropHorizontal();
				try {
					CCL_Crossings();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					ED();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	public ArrayList<ArrayList<String>> proses(File file) throws IOException{
		File index = new File("charactersUji");
		try{
		String[]entries = index.list();
		for(String s: entries){
		    File currentFile = new File(index.getPath(),s);
		    currentFile.delete();
		}
		}
		catch(Exception e){
			System.out.println("");
		}
		index.delete();
		
		index.mkdir();
		img = ImageIO.read(file);
		ccl.charImg = new ArrayList<>();
		
		//gaussian filter
		GaussianFilter gf = new GaussianFilter();
		gf.prosesGaussian(img);
		
		//sharpening
		BufferedImageSharpen bis = new BufferedImageSharpen();
		img = bis.process(img);
		
		//crop
		croppedImg = img.getSubimage(5, 60, 495, img.getHeight()-60-1);
		
		//thresholding
		int wCroppedImg = croppedImg.getWidth();
		int hCroppedImg = croppedImg.getHeight();
		BufferedImage newGreyscalledImg = new BufferedImage(wCroppedImg, hCroppedImg, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = newGreyscalledImg.createGraphics();
		g.drawImage(croppedImg, 0, 0, wCroppedImg, hCroppedImg, null);
		g.dispose();

		int wGsImg = newGreyscalledImg.getWidth();
		int hGsImg = newGreyscalledImg.getHeight();

		int nilaiThreshold = 105;
		for (int i = 0; i < hGsImg; i++) {
			for (int j = 0; j < wGsImg; j++) {
				Color c = new Color(newGreyscalledImg.getRGB(j, i));
				if (c.getRed() < nilaiThreshold && c.getGreen() < nilaiThreshold && c.getBlue() < nilaiThreshold) {
					newGreyscalledImg.setRGB(j, i, Color.black.getRGB());
				}
				else
				{
					newGreyscalledImg.setRGB(j, i, Color.white.getRGB());
				}
			}
		}
		newBWImg = new BufferedImage(wCroppedImg, hCroppedImg, BufferedImage.TYPE_INT_ARGB);
		Graphics g1 = newBWImg.createGraphics();
		g1.drawImage(newGreyscalledImg, 0, 0, wCroppedImg, hCroppedImg, null);
		g1.dispose();
		
		//CCL dan Crossings
		cariPixelHorizontal();
		drawLineHorizontal();
		cropHorizontal();
		CCL_Crossings();
		
		ArrayList<ArrayList<String>> hasilAkhir = new ArrayList<>();
		//Euclidean Distance
		try {
			hasilAkhir = ED();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return hasilAkhir;
	}
}
