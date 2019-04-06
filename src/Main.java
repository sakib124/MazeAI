import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {
	
	JFrame f;
	private int units = 20;
	private int delay = 25;
	private double dense = .50;
	private double density = (units*units)*.50;
	private int x_start = -1;
	private int y_start = -1;
	private int x_finish = -1;
	private int y_finish = -1;
	private int option = 0;
	private int explorations = 0;
	private int length = 0;
	private int width = 655;
	private final int height = 890;
	private final int mazeSize = 600;
	private int unitSize = mazeSize/units;
	private String[] options = {"Start Point","Finish Point","Add Wall", "Remove Wall"};
	private boolean solving = false;
	
	//Initializing all of the panels and controls for the main menu
	Node[][] map;
	Algorithm Alg = new Algorithm();
	Random rand = new Random();
	JSlider size = new JSlider(1,5,2);
	JLabel lblOption = new JLabel("Options:");
	JLabel lblSize = new JLabel("Size:");
	JLabel lblUnits = new JLabel(units+"x"+units);
	JLabel lblExplorations = new JLabel("Explorations: "+explorations);
	JLabel lblLength = new JLabel("Path Length: "+length);
	JButton btnSearch = new JButton("Start Search");
	JButton btnGenMaze = new JButton("Generate Maze");
	JButton btnReset = new JButton("Reset");
	JButton btnClear = new JButton("Clear");
	JComboBox cmbOption = new JComboBox(options);
	JPanel pnlOption = new JPanel();
	Map canvas;
	Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public static void main(String[] args) {
		new Main();
	}

	//The main class for when the program starts up
	public Main() {	
		clearMaze();
		initialize();
	}
	
	//Generating maze method
	public void generateMaze() {	
		clearMaze();	
		for(int i = 0; i < density; i++) {
			Node current;
			do {
				int x = rand.nextInt(units);
				int y = rand.nextInt(units);
				current = map[x][y];	
			} while(current.getType()==2);	
			current.setType(2);	
		}
	}
	
	//Clearing maze method
	public void clearMaze() {	
		x_finish = -1;	
		y_finish = -1;
		x_start = -1;
		y_start = -1;
		map = new Node[units][units];	
		for(int x = 0; x < units; x++) {
			for(int y = 0; y < units; y++) {
				map[x][y] = new Node(3,x,y);	
			}
		}
		reset();	
	}

	//Resetting maze method
	public void resetMaze() {	
		for(int x = 0; x < units; x++) {
			for(int y = 0; y < units; y++) {
				Node current = map[x][y];
				if(current.getType() == 4 || current.getType() == 5)	
					map[x][y] = new Node(3,x,y);	
			}
		}
		if(x_start > -1 && y_start > -1) {	
			map[x_start][y_start] = new Node(0,x_start,y_start);
			map[x_start][y_start].setHops(0);
		}
		if(x_finish > -1 && y_finish > -1)
			map[x_finish][y_finish] = new Node(1,x_finish,y_finish);
		reset();	
	}

	//initializing method
	private void initialize() {	
		//Initializing the main frame
		f = new JFrame();
		f.setVisible(true);
		f.setResizable(false);
		f.setSize(width,height);
		f.setTitle("Maze");
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		//Placing all of the items for the GUI
		pnlOption.setBorder(BorderFactory.createTitledBorder(etched));
		
		pnlOption.setLayout(null);
		pnlOption.setBounds(10,650,625,190);
		
		btnSearch.setBounds(25,40, 120, 25);
		pnlOption.add(btnSearch);
		
		
		btnGenMaze.setBounds(175,40,120,25);
		pnlOption.add(btnReset);
		
		
		btnReset.setBounds(325,40, 120, 25);
		pnlOption.add(btnGenMaze);
		
		
		btnClear.setBounds(475,40, 120, 25);
		pnlOption.add(btnClear);
		
		
		lblOption.setBounds(25,95,120,25);
		pnlOption.add(lblOption);
		
		
		cmbOption.setBounds(25,120,120,25);
		pnlOption.add(cmbOption);
		
		
		lblSize.setBounds(175,95,120,25);
		pnlOption.add(lblSize);
		
		size.setMajorTickSpacing(10);
		size.setBounds(175,120,120,25);
		pnlOption.add(size);
		
		lblUnits.setBounds(300,120,40,25);
		pnlOption.add(lblUnits);
		
		
		lblExplorations.setBounds(350,120,100,25);
		pnlOption.add(lblExplorations);
		
		
		lblLength.setBounds(475,120,100,25);
		pnlOption.add(lblLength);
		
		
		f.getContentPane().add(pnlOption);
		
		canvas = new Map();
		canvas.setBounds(25, 20, mazeSize+1, mazeSize+1);
		f.getContentPane().add(canvas);
		
		//Setting listeners for each of the buttons 
		btnSearch.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				if((x_start > -1 && y_start > -1) && (x_finish > -1 && y_finish > -1))
					solving = true;
			}
		});
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMaze();
				Update();
			}
		});
		btnGenMaze.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateMaze();
				Update();
			}
		});
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearMaze();
				Update();
			}
		});
		cmbOption.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				option = cmbOption.getSelectedIndex();
			}
		});
		size.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				units = size.getValue()*10;
				clearMaze();
				reset();
				Update();
			}
		});
		
		startSearch();	
	}
	
	//Method that calls the A* search algorithm
	public void startSearch() {	
		if(solving) {
			Alg.AStar();
		}
		pause();	
	}
	
	//Allows program to sleep
	public void pause() {	
		int i = 0;
		while(!solving) {
			i++;
			if(i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch(Exception e) {}
		}
		startSearch();	
	}
	
	//Method to update all of the components that user can change
	public void Update() {	
		density = (units*units)*dense;
		unitSize = mazeSize/units;
		canvas.repaint();
		lblUnits.setText(units+"x"+units);
		lblLength.setText("Path Length: "+length);
		lblExplorations.setText("Explorations: "+explorations);
	}
	
	//Reset method for the game
	public void reset() {	
		solving = false;
		length = 0;
		explorations = 0;
	}
	
	public void delay() {	
		try {
			Thread.sleep(delay);
		} catch(Exception e) {}
	}
	
	//The map class holding all of the graphics
	class Map extends JPanel implements MouseListener, MouseMotionListener{	
		
		public Map() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			//Initializing all of the icons/images
			Image img = new ImageIcon("res/mario.png").getImage();
			Image img1 = new ImageIcon("res/flag2.png").getImage();
			Image img2 = new ImageIcon("res/brick.png").getImage();
			Image img3 = new ImageIcon("res/grass (1).png").getImage();
			Image img4 = new ImageIcon("res/white.png").getImage();
			Image img5 = new ImageIcon("res/blue.png").getImage();
		    
		    //For loops and select cases for when the algorithm sends back which type of image to use
			for(int x = 0; x < units; x++) {	
				for(int y = 0; y < units; y++) {
					switch(map[x][y].getType()) {
						case 0:
							g.drawImage(img, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
						case 1:
							g.drawImage(img1, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
						case 2:
							g.drawImage(img2, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
						case 3:
							g.drawImage(img3, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
						case 4:
							g.drawImage(img4, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
						case 5:
							g.drawImage(img5, x*unitSize,y*unitSize,unitSize,unitSize,this);
							break;
					}
					
					g.setColor(Color.BLACK);
					g.drawRect(x*unitSize,y*unitSize,unitSize,unitSize);
					
				}
			}
		}

		//The method that allows user to drag items such as walls on the maze
		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				int x = e.getX()/unitSize;	
				int y = e.getY()/unitSize;
				Node current = map[x][y];
				if((option == 2 || option == 3) && (current.getType() != 0 && current.getType() != 1))
					current.setType(option);
				Update();
			} catch(Exception z) {}
		}

		//The method that allows user to press and add items such as walls on the maze
		@Override
		public void mousePressed(MouseEvent e) {
			resetMaze();	
			try {
				int x = e.getX()/unitSize;	
				int y = e.getY()/unitSize;
				Node current = map[x][y];
				switch(option ) {
					case 0: {	
						if(current.getType()!=2) {	
							if(x_start > -1 && y_start > -1) {	
								map[x_start][y_start].setType(3);
								map[x_start][y_start].setHops(-1);
							}
							current.setHops(0);
							x_start = x;	
							y_start = y;
							current.setType(0);	
						}
						break;
					}
					case 1: {
						if(current.getType()!=2) {	
							if(x_finish > -1 && y_finish > -1)	
								map[x_finish][y_finish].setType(3);
							x_finish = x;	
							y_finish = y;
							current.setType(1);	
						}
						break;
					}
					default:
						if(current.getType() != 0 && current.getType() != 1)
							current.setType(option);
						break;
				}
				Update();
			} catch(Exception z) {}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
class Node {
		
		
		private int unitType = 0;
		private int hops;
		private int x;
		private int y;
		private int lastX;
		private int lastY;
		private double dToEnd = 0;
	
		
		public Node(int type, int x, int y) {	
			unitType = type;
			this.x = x;
			this.y = y;
			hops = -1;
		}
		
		//Method that caluclates Euclidian distance
		public double getEuclidDist() {		
			int xdif = Math.abs(x-x_finish);
			int ydif = Math.abs(y-y_finish);
			dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
			return dToEnd;
		}
		
		public int getX() {return x;}		
		public int getY() {return y;}
		public int getLastX() {return lastX;}
		public int getLastY() {return lastY;}
		public int getType() {return unitType;}
		public int getHops() {return hops;}
		
		public void setType(int type) {unitType = type;}		
		public void setLastNode(int x, int y) {lastX = x; lastY = y;}
		public void setHops(int hops) {this.hops = hops;}
	}

	//Algorithm class holds all the rules and conditions for the A* search algorithm
	class Algorithm {
		//The main method for the algorithm which calls other methods
		public void AStar() {
			ArrayList<Node> priority = new ArrayList<Node>();
			priority.add(map[x_start][y_start]);
			while(solving) {
				if(priority.size() <= 0) {
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;
				ArrayList<Node> explored = exploreNeighbors(priority.get(0),hops);
				if(explored.size() > 0) {
					priority.remove(0);
					priority.addAll(explored);
					Update();
					delay();
				} else {
					priority.remove(0);
				}
				sortQue(priority);
			}
		}
		
		//Sorts the queue which is used to decide which is the next best move
		public ArrayList<Node> sortQue(ArrayList<Node> sort) {
			int c = 0;
			while(c < sort.size()) {
				int sm = c;
				for(int i = c+1; i < sort.size(); i++) {
					if(sort.get(i).getEuclidDist()+sort.get(i).getHops() < sort.get(sm).getEuclidDist()+sort.get(sm).getHops())
						sm = i;
				}
				if(c != sm) {
					Node temp = sort.get(c);
					sort.set(c, sort.get(sm));
					sort.set(sm, temp);
				}	
				c++;
			}
			return sort;
		}
		
		//Method that allows the search to find its neighbours and see which one is best
		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	
			ArrayList<Node> explored = new ArrayList<Node>();	
			for(int a = -1; a <= 1; a++) {
				for(int b = -1; b <= 1; b++) {
					int xbound = current.getX()+a;
					int ybound = current.getY()+b;
					if((xbound > -1 && xbound < units) && (ybound > -1 && ybound < units)) {	
						Node neighbor = map[xbound][ybound];
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	
							explore(neighbor, current.getX(), current.getY(), hops);	
							explored.add(neighbor);	
						}
					}
				}
				
			}
			return explored;
			
		}
		
		//Backtracking method
		public void backtrack(int lx, int ly, int hops) {	
			length = hops;
			while(hops > 1) {	
				Node current = map[lx][ly];
				current.setType(5);
				lx = current.getLastX();
				ly = current.getLastY();
				hops--;
				
			}
			solving = false;
		}
		
		//Keeps track of the amount of explorations
		public void explore(Node current, int lastx, int lasty, int hops) {	
			if(current.getType()!=0 && current.getType() != 1)	
				current.setType(4);	
			current.setLastNode(lastx, lasty);	
			current.setHops(hops);	
			explorations++;
			if(current.getType() == 1) {	
				backtrack(current.getLastX(), current.getLastY(),hops);	
			}
			
		}
		
		
	}
	
	
}