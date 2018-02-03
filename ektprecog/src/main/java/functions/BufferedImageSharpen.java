package functions;


import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;

import java.awt.image.Kernel;

public class BufferedImageSharpen {


    public BufferedImage process(BufferedImage image){
    	
        Kernel kernel = new Kernel(3, 3,
                new float[]{
                	-1, -1, -1,
                    -1, 9, -1,
                    -1, -1, -1});
        BufferedImageOp op = new ConvolveOp(kernel);
        image = op.filter(image, null);

		return image;

    }


}