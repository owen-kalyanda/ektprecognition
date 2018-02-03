package functions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GaussianFilter {
	
	ArrayList<Double> arrayKernel = new ArrayList<>();
	
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
	
	private void CariGaussianKernel() {
		int y = -7;
		int sigma = 1;
	    for (int i = 0; i <= 14; i++) {
	    	int x = -7;
	      for (int j = 0; j <= 14; j++) {
	    	  double kernel = Math.exp(-0.5*(Math.pow(x, 2)+Math.pow(y, 2)/Math.pow(sigma, 2)))/(2*Math.PI*Math.pow(sigma, 2));
	    	  arrayKernel.add(kernel);
	    	  x++;
	      }
	      y++;
	    }
	}
	
	public void prosesGaussian(BufferedImage img){
		CariGaussianKernel();
		
		int w = img.getWidth();
	    int h = img.getHeight();
	    BufferedImage paddedImg = new BufferedImage(w + 14, h + 14, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graph = paddedImg.createGraphics();
	    graph.setColor(Color.WHITE);
	    graph.fillRect(0, 0, w+14, h+14);
	    graph.drawImage(img, null, 7, 7);
	    graph.dispose();
	    
	    int m = 0;
	    for (int i = 0; i < h ; i++) {
	    	int n = 0;
	      for (int j = 0; j < w ; j++) {
	    	  int o = 0;
	    	  double rn = 0;
	    	  double gn = 0;
	    	  double bn = 0;
	        for (int k = 0; k < 15; k++){
	        	for (int l = 0; l < 15; l++){
	        		int pixel = paddedImg.getRGB(j, i);
	        		double red = getRed(pixel);
	        		double green = getGreen(pixel);
	        		double blue = getBlue(pixel);
	        		rn = rn + (red * arrayKernel.get(o));
	        		gn = gn + (green * arrayKernel.get(o));
	        		bn = bn + (blue * arrayKernel.get(o));
	        		
	        		j++;
	        		o++;
	        	}
	        	j=n;
	        	i++;
	        }
	        int rnn = (int) Math.round(rn);
    		int gnn = (int) Math.round(gn);
    		int bnn = (int) Math.round(bn);
	        Color newpixel = new Color(rnn, gnn, bnn);
	        img.setRGB(n, m, newpixel.getRGB());
	        i=m;
	        n++;
	      }
	      m++;
	    }
	}
}
