package Main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;

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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.border.TitledBorder;

import com.mysql.fabric.xmlrpc.base.Array;

import functions.BufferedImageSharpen;
import functions.CCL;
import functions.Crossings;
import functions.GaussianFilter;

import javax.swing.UIManager;
import javax.swing.SwingConstants;

public class Train extends JFrame {
	int pushedCrop = 0;
	int pushedOpen = 0;
	int countCross = 0;
	static int numImg = 0;
	File file = null;
	BufferedImage newImg;
	BufferedImage croppedImg;
	BufferedImage newCroppedImg;
	BufferedImage newCroppedImg2;
	BufferedImage img;
	static BufferedImage newBWImg;
	BufferedImage newGreyscalledImg;
	int panjangBaris;
	int batasAtas;
	int batasBawah;
	ArrayList<Integer> arrayPixelHorizontal = new ArrayList<>();
	ArrayList<Integer> arrayPixelVertikal = new ArrayList<>();
	ArrayList<Integer> arrayPixelVertikalPutih = new ArrayList<>();
	ArrayList<Integer> arrayNilaiRGB = new ArrayList<>();
	ArrayList<Double> arrayKernel = new ArrayList<>();
	ArrayList<Integer> arrayPixelPadded = new ArrayList<>();
	ArrayList<Integer> arrayY = new ArrayList<>();
	ArrayList<BufferedImage> croppedHImg= new ArrayList<>();
	ArrayList<Double> horizontalUji = new ArrayList<>();
	ArrayList<Double> horizontalUji2 = new ArrayList<>();
    ArrayList<Double> vertikalUji = new ArrayList<>();
	ArrayList<Double> vertikalUji2 = new ArrayList<>();
	PrintStream out;
	JFrame frame2;
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
	JButton btnCrossings = new JButton("Crossings");
	JButton btnUji = new JButton("Uji");
	public static int nomorImg = 0;
	static ArrayList<ArrayList<BufferedImage>> arrImgCropped = new ArrayList<>();
	static ArrayList<Integer> imgW = new ArrayList<>();
	static ArrayList<Integer> imgH = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Train frame = new Train();
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
	
	private void cariPixelHorizontal() {
	    int w = newBWImg.getWidth();
	    int h = newBWImg.getHeight();
	    arrayPixelHorizontal.clear();

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
		arrayPixelHorizontal.clear();
		
		}
	
	private ArrayList<BufferedImage> cropHorizontal() throws IOException {
		JFrame frame = new JFrame("Cropped Horizontal");
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
				JLabel lblHCropped = new JLabel();
				Border border = BorderFactory.createLineBorder(Color.BLUE, 1);
				lblHCropped.setBorder(border);
				lblHCropped.setBounds(10, 10 + a, w, h3);
				frame.getContentPane().add(lblHCropped);
				lblHCropped.setIcon(new ImageIcon(croppedHImg.get(b)));
				h1 = h2;
				a = a + 40;
				b++;
			}
	
		}
		frame.setVisible(true);
		
		frame2 = new JFrame("Hasil CCL");
		frame2.setSize(1240, 768);
		frame2.getContentPane().setLayout(null);

		int y = 0;
		int temp = 0;
		
		ArrayList<BufferedImage> imgChar = new ArrayList<>();
		for (int i = 0; i < croppedHImg.size(); i++) {
			ccl.prosesCCL(croppedHImg.get(i), frame2, y);
			if(i == 0){
				numImg = ccl.charImg.size();
			}
			for (int j = temp; j < ccl.charImg.size(); j++) {
				imgChar.add(ccl.charImg.get(j));
				ImageIO.write(ccl.charImg.get(j), "png", new File("characters\\charcomponent-" + j + ".png"));
			}
			y = y + 48;
			ccl.a = 0;
			temp = ccl.charImg.size();
		}
		
		tfImgNum = new JTextField();
		tfCharacter = new JTextField();
		tfImgNum.setBounds(30, 650, 30, 30);
		tfCharacter.setBounds(70, 650, 30, 30);
		btnCrossings.setBounds(110, 650, 100, 50);
		btnUji.setBounds(220, 650, 100, 50);
		frame2.getContentPane().add(tfImgNum);
		frame2.getContentPane().add(tfCharacter);
		frame2.getContentPane().add(btnCrossings);
		frame2.getContentPane().add(btnUji);
		frame2.setVisible(true);
		
		return imgChar;
	}

	public BufferedImage getNewImg() {
        return newImg;
    }
	
	/**
	 * Create the frame.
	 */
	public Train() {
		setTitle("preProcess");
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
		tfNilaiThreshold.setText("100");
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
					System.out.println("Input tidak jadi dimasukkan");
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
				int h = Integer.parseInt(tfHeightCrop.getText());
				croppedImg = img.getSubimage(1, y, w, img.getHeight()-y-1);
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
				try {
					cropHorizontal();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnCrossings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Crossings cr = new Crossings();
				int nomorImgtoDB = Integer.parseInt(tfImgNum.getText());
				cr.prosesCrossings(CCL.charImg.get(nomorImgtoDB), frame2, lblChar, countCross, tfCharacter, nomorImgtoDB, numImg);
			}
		});
	}
	
	public ArrayList<BufferedImage> train(File file) throws IOException{
		
		File index = new File("characters");
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
		
		img = ImageIO.read(file);
		ccl.charImg = new ArrayList<>();
		
		//gaussian filter
		GaussianFilter gf = new GaussianFilter();
		gf.prosesGaussian(img);
		
		//sharpening
		BufferedImageSharpen bis = new BufferedImageSharpen();
		img = bis.process(img);
		
		//crop
		croppedImg = img.getSubimage(5, 60, 500, img.getHeight()-60-1);
		
		//thresholding
		int wCroppedImg = croppedImg.getWidth();
		int hCroppedImg = croppedImg.getHeight();
		BufferedImage newGreyscalledImg = new BufferedImage(wCroppedImg, hCroppedImg, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = newGreyscalledImg.createGraphics();
		g.drawImage(croppedImg, 0, 0, wCroppedImg, hCroppedImg, null);
		g.dispose();

		int wGsImg = newGreyscalledImg.getWidth();
		int hGsImg = newGreyscalledImg.getHeight();

		int nilaiThreshold = Integer.parseInt(tfNilaiThreshold.getText());
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
		
		
		//CCL
		cariPixelHorizontal();
		drawLineHorizontal();
		
		File uploadDir = new File("characters");
        uploadDir.mkdir();

		return cropHorizontal();
		
		
	}
}
