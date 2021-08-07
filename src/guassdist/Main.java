package guassdist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Main
{
	final Panel p;
	final Frame f;
	
	Main()
	{
		p = new Panel();
		f = new Frame();
	}

	public static void main(String[] args)
	{
		Main guass = new Main();
	}
}

@SuppressWarnings("serial")
class Panel
{
	private static JPanel panel;
	private Boolean createNew;
	private ArrayList<particle> particleList;
	private field f;
	private Random rand;
	final Runnable rebuild;
	
	Panel()
	{
		createNew = true;
		particleList = new ArrayList<particle>();
		f = new field();
		rand = new Random();
		
		panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D)g;
				super.paintComponent(g2d);
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        
		        for (particle p : particleList)
		        {
		            g2d.setColor(Color.BLACK);
		            g2d.fill(p);
		            
		            g2d.setColor(Color.RED);
		            g2d.draw(new Line2D.Float(0f, 125f, 500f, 125f));
		        }
			}
		};
		
		panel.setOpaque(true);
		panel.setSize(new Dimension(500, 250));
		panel.setLocation(0, 125);
		panel.setBackground(new Color(255, 255, 255));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel.setFocusable(true);
		panel.requestFocus();
		panel.setLayout(null);
		
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        rebuild = new Runnable()
        {
            public void run()
            {
            	if (createNew)
            	{
            		createNew = false;
            		particleList.add(new particle());
            		//System.out.println("new");
            	}
            	else
            	{
            		particle p = particleList.get(particleList.size() - 1);
            		if (f.checkY((int) p.x, (int) p.y))
            		{
            			//System.out.println("fall: " + Float.toString(p.x) + " / " + Float.toString(p.y));
            			int r = rand.nextInt(2);
                		int direct = (r == 0) ? -5 : 5;
                		
                		if (f.checkX((int) p.x, (int) p.y, direct))
                		{
                			p.fall();
                			if (direct == 5) p.right();
                			else p.left();
                		}
                		else p.fall();
            		}
            		else
            		{
            			if (p.y == 125)
            			{
            				//System.out.println("\nFINISHED");
            				executorService.shutdownNow();
            			}
            			f.place((int) p.x, (int) p.y);
            			createNew = true;
            			//System.out.println("placed");
            			//System.out.println(particleList.size());
            		}
            	}
                panel.repaint();
            }
        };
        executorService.scheduleAtFixedRate(rebuild, 0, 5, TimeUnit.MILLISECONDS);
	}

	public static JPanel getPanel()
	{
		return panel;
	}
	
	public void createNewstate()
	{
		createNew = true;
	}
}

class Frame
{
	private JFrame frame;
	
	Frame()
	{
		frame = new JFrame();
		JPanel p = Panel.getPanel();
		//frame.setContentPane(p);
	    frame.setTitle("The Gaussian Distribution");
	    frame.setPreferredSize(new Dimension(500, 500));
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setBackground(new Color(255, 255, 255));
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    frame.setFocusable(false);
	    
	    frame.add(p);
	}
}

class field
{
	private int[][] map;
	
	field()
	{
		this.map = new int[100][50];
		for (int i = 0; i < 100; i++)
		{
			this.map[i][0] = 1;
		}
	}
	
	private Integer translateY(int y)
	{
		int newY = 250 - y;
		return newY;
	}
	
	public Boolean checkY(int x, int y)
	{
		Boolean state = false;
		int newY = translateY(y+5);
		if (map[x/5][newY/5] == 0) state = true;
		return state;
	}
	
	public Boolean checkX(int x, int y, int direction)
	{
		Boolean state = false;
		int newY = translateY(y+5);
		if (map[(x+direction)/5][newY/5] == 0) state = true;
		return state;
	}
	
	public void place(int x, int y)
	{
		int newY = translateY(y);
		map[x/5][newY/5] = 1;
	}
}

@SuppressWarnings("serial")
class particle extends Rectangle2D.Float
{
	particle()
	{
		setFrame(250, 0, 5, 5);
	}
	
	public void fall()
	{
		this.y+=5;
	}
	
	public void right()
	{
		if (this.x < 500) this.x+=5;
	}
	
	public void left()
	{
		if (this.x > 0) this.x-=5;
	}
}
