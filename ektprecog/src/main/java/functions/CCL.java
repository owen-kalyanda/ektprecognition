package functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import Main.Train;

public class CCL
{
    private int[][] _board;
    private BufferedImage _input;
    private Graphics inputGD;
    private int _width;
    private int _height;
    private int backgroundColor;
    public static int b = 0;
    public static int a = 0;
    public static ArrayList<BufferedImage>charImg = new ArrayList<>();
    
    public static int getRed(int pixel){
		int red = (pixel >> 16) & 0xff;
		return red;
	}

    public Map<Integer, BufferedImage> Process(BufferedImage input, int bgColor)
    {
    	backgroundColor = bgColor;
        _input = input;
        _width = input.getWidth();
        _height = input.getHeight();
        _board = new int[_width][];
        for(int i = 0;i < _width;i++)
        	_board[i] = new int[_height];

        Map<Integer, List<Pixel>> patterns = Find();
        Map<Integer, BufferedImage> images = new HashMap<Integer, BufferedImage>();

        inputGD = _input.getGraphics();
        inputGD.setColor(Color.BLUE);
        for(Integer id : patterns.keySet())
        {
            BufferedImage bmp = CreateBitmap(patterns.get(id));
            images.put(id, bmp);
        }
        inputGD.dispose();

        return images;
    }

    protected boolean CheckIsBackGround(Pixel currentPixel)
    {
    	return currentPixel.color == backgroundColor;
    }

    private static int Min(List<Integer> neighboringLabels, Map<Integer, Label> allLabels) {
    	if(neighboringLabels.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = allLabels.get(neighboringLabels.get(0)).GetRoot().name;
    	for(Integer n : neighboringLabels) {
    		int curVal = allLabels.get(n).GetRoot().name;
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }
    
    private static int Min(List<Pixel> pattern, boolean xOrY) {
    	if(pattern.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }

    private static int Max(List<Pixel> pattern, boolean xOrY) {
    	if(pattern.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret > curVal ? ret : curVal);
    	}
    	return ret;
    }

    //mencari label tiap pixel
    private Map<Integer, List<Pixel>> Find()
    {
        int labelCount = 1;
        Map<Integer, Label> allLabels = new HashMap<Integer, Label>();

        for (int i = 0; i < _width; i++)
        {
            for (int j = 0; j < _height; j++)
            {
                Pixel currentPixel = new Pixel(i, j, _input.getRGB(i, j));

                if (CheckIsBackGround(currentPixel))
                {
                    continue;
                }

                List<Integer> neighboringLabels = GetNeighboringLabels(currentPixel);
                int currentLabel;

                if (neighboringLabels.isEmpty())
                {
                    currentLabel = labelCount;
                    allLabels.put(currentLabel, new Label(currentLabel));
                    labelCount++;
                }
                else
                {
                    currentLabel = Min(neighboringLabels, allLabels);
                    Label root = allLabels.get(currentLabel).GetRoot();

                    for (Integer neighbor : neighboringLabels)
                    {
                        if (root.name != allLabels.get(neighbor).GetRoot().name)
                        {
                            allLabels.get(neighbor).Join(allLabels.get(currentLabel));
                        }
                    }
                }

                _board[i][j] = currentLabel;
            }
        }


        Map<Integer, List<Pixel>> patterns = AggregatePatterns(allLabels);

        return patterns;
    }
    
    //untuk mengatur jarak pemotongan
    private List<Integer> GetNeighboringLabels(Pixel pix)
    {
        List<Integer> neighboringLabels = new ArrayList<Integer>();
        
        
        for (int j = pix.x - 1; j <= pix.x + 2 && j < _width - 1; j++)
        {
            for (int i = pix.y - 1; i <= pix.y + 10  && i < _height - 1; i++)
            {
                if (i > -1 && j > -1 && _board[j][i] != 0)
                {
                    neighboringLabels.add(_board[j][i]);
                }
            }
        }

        return neighboringLabels;
    }

    private Map<Integer, List<Pixel>> AggregatePatterns(Map<Integer, Label> allLabels)
    {
        Map<Integer, List<Pixel>> patterns = new HashMap<Integer, List<Pixel>>();

        for (int i = 0; i < _height; i++)
        {
            for (int j = 0; j < _width; j++)
            {
                int patternNumber = _board[j][i];

                if (patternNumber != 0)
                {
                    patternNumber = allLabels.get(patternNumber).GetRoot().name;

                    if (!patterns.containsKey(patternNumber))
                    {
                        patterns.put(patternNumber, new ArrayList<Pixel>());
                    }

                    patterns.get(patternNumber).add(new Pixel(j, i, _input.getRGB(j, i)));
                }
            }
        }

        return patterns;
    }

    private BufferedImage CreateBitmap(List<Pixel> pattern)
    {
        int minX = Min(pattern, true);
        int maxX = Max(pattern, true);

        int minY = Min(pattern, false);
        int maxY = Max(pattern, false);
        
        int width = maxX + 1 - minX;
        int height = maxY + 1 - minY;

        BufferedImage bmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (Pixel pix : pattern)
        {
            bmp.setRGB(pix.x - minX, pix.y - minY, pix.color); //shift position by minX and minY
        }
        
        inputGD.drawRect(minX, minY, maxX-minX, maxY-minY);

        return bmp;
    }

    public static String getBaseFileName(String fileName) {
    	return fileName.substring(0, fileName.indexOf('.'));
    }
    
    public static String getFileNameExtension(String fileName) {
    	return fileName.substring(fileName.indexOf('.') + 1);
    }
    
    public BufferedImage getProcessedImage() {
    	return _input;
    }
    
    public class Pixel
    {
    	public int x;
    	public int y;
    	public int color;

        public Pixel(int x, int y, int color)
        {
            this.x = x;
            this.y = y;
            this.color = color;
        }

    }
    
    public class Label {
        public int name;

        public Label Root;

        public int Rank;

        public Label(int Name)
        {
            this.name = Name;
            this.Root = this;
            this.Rank = 0;
        }

        public int getName() {
    		return name;
    	}

    	public void setName(int name) {
    		this.name = name;
    	}

    	public Label getRoot() {
    		return Root;
    	}

    	public void setRoot(Label root) {
    		Root = root;
    	}

    	public int getRank() {
    		return Rank;
    	}

    	public void setRank(int rank) {
    		Rank = rank;
    	}

    	Label GetRoot()
        {
            if (this.Root != this)
            {
                this.Root = this.Root.GetRoot();
            }

            return this.Root;
        }

        void Join(Label root2)
        {
            if (root2.Rank < this.Rank)
            {
                root2.Root = this;
            }
            else 
            {
                this.Root = root2;
                if (this.Rank == root2.Rank)
                {
                    root2.Rank++;
                }
            }
        }
    }
    
    public static void main(String[] args) {
    	
    }
    
    public void prosesCCL(BufferedImage img, JFrame frame, int y){
    	
    	CCL ccl = new CCL();
    	try {
    		int bgColor = 0xFFFFFFFF; 
    		BufferedImage bnw = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    		Graphics2D graph = bnw.createGraphics();
		    graph.drawImage(img, null, 0, 0);
		    graph.dispose();
    		// TODO: Obtain background color.
    
    		Map<Integer, BufferedImage> components = ccl.Process(bnw, bgColor);

    			int [] testts = new int[components.keySet().size()];
    			int iter=0;
    			for(Integer c : components.keySet()) {
    				
        			testts[iter]=c;
        		iter++;
    			}
    			
    			Arrays.sort(testts);
    		
    			for(Integer c : testts) {
    				if(components.get(c).getWidth() > 3 || components.get(c).getHeight() > 3){
        			charImg.add(components.get(c));
            		JLabel label = new JLabel();
            		JLabel label2 = new JLabel();
            		label.setBounds(10 + a, 10 + y, components.get(c).getWidth(), components.get(c).getHeight());
            		label.setIcon(new ImageIcon(components.get(c)));
            		label2.setBounds(10 + a, 35 + y, 30, 10);
            		label2.setText(Integer.toString(b));
            		frame.add(label);
            		frame.add(label2);
            		a = a + 45;
            		b++;
            		ImageIO.write(components.get(c), ".jpg", new File("characters\\charcomponent-" + c + ".jpg"));
    				}
        		}
    			
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}