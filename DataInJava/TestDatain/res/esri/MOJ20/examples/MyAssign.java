
import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.cs.geom.Envelope;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.util.ArrayList;

public class MyAssign extends JFrame
{
  static Map map = new Map();
  static boolean fullMap = true;
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();

  static com.esri.mo2.map.dpy.Layer layer4;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  Toc toc = new Toc();
  String s1 = "C:\\esri\\MOJ20\\Samples\\Data\\World\\country.shp";
  String s2 = "C:\\esri\\MOJ20\\Samples\\Data\\World\\MyShapeFile.shp";
  String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton pointer = new JButton(new ImageIcon("pointer.gif"));
  JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));
  Arrow arrow = new Arrow();
  ActionListener lis;
  ActionListener layerlis;
  TocAdapter mytocadapter;
  Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage("hotlink.gif");  // 16x16 gif file
  java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new Point(6,30),"bolt");
  MyPickAdapter picklis = new MyPickAdapter();
  Identify hotlink = new Identify(); //the Identify class implements a PickListener,

  static String selected_country = null;
  static String region = null;
  static String capital_city = null;

  class MyPickAdapter implements PickListener
  {   //implements hotlink
    public void beginPick(PickEvent pe){};
    // this fires even when you click outside the states layer
    public void endPick(PickEvent pe){}
    public void foundData(PickEvent pe)
    {
      //fires only when a layer feature is clicked
      FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
      com.esri.mo2.data.feat.Cursor c = pe.getCursor();
      Feature f = null;
      Fields fields = null;

      if (c != null)
        f = (Feature)c.next();

      fields = f.getFields();
      System.out.println("****" + fields);

      selected_country = (String)f.getValue(1);
      region = (String)f.getValue(5);
      capital_city = (String)f.getValue(7);
		System.out.println("Checkpoint :: :: ::" + selected_country);
      try {
    		HotPick hotpick = new HotPick();//opens dialog window with Duke in it
    		hotpick.setVisible(true);
          } catch(Exception e){}
    }
  };

  static Envelope env;

  public MyAssign()
  {
    super("My Assignment");
    this.setSize(1000,500);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);

    ActionListener lisZoom = new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        fullMap = false;
      }
    }; // can change a boolean here

    ActionListener lisFullExt = new ActionListener()
    {
    	public void actionPerformed(ActionEvent ae)
    	{
			fullMap = true;
		}
	};
    		// next line gets ahold of a reference to the zoomin button
    JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
    JButton zoomFullExtentButton = (JButton)zptb.getActionComponent("ZoomToFullExtent");
    JButton zoomToSelectedLayerButton = (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
    zoomInButton.addActionListener(lisZoom);
    zoomFullExtentButton.addActionListener(lisFullExt);
    zoomToSelectedLayerButton.addActionListener(lisZoom);

    complistener = new ComponentAdapter ()
    {
    	public void componentResized(ComponentEvent ce)
    	{
    		if(fullMap)
    		{
    			map.setExtent(env);
    			map.zoom(1.0);    //scale is scale factor in pixels
    			map.redraw();
    		}
    	}
    };
    addComponentListener(complistener);
    lis = new ActionListener()
    {
    	public void actionPerformed(ActionEvent ae)
    	{
    		Object source = ae.getSource();

      		if (source == hotjb)
      		{
				hotlink.setCursor(boltCursor);
        		map.setSelectedTool(hotlink);
      		}
      		else if(source == pointer)
      		{
      			map.setSelectedTool(arrow);
      		}
    	}
    };

    layerlis = new ActionListener()
    {
    	public void actionPerformed(ActionEvent ae)
    	{
      		Object source = ae.getSource();
      		if (source instanceof JMenuItem)
      		{
        		String arg = ae.getActionCommand();
        		if(arg == "add layer")
        		{
          			try {
            			AddLyrDialog aldlg = new AddLyrDialog();
            			aldlg.setMap(map);
            			aldlg.setVisible(true);
          				} catch(IOException e){}
				}

				else if(arg == "remove layer")
				{
  					try {
    					com.esri.mo2.map.dpy.Layer dpylayer =
   						legend.getLayer();
    					map.getLayerset().removeLayer(dpylayer);
    					map.redraw();
    					remlyritem.setEnabled(false);
    					propsitem.setEnabled(false);
    					stb.setSelectedLayer(null);
    					zptb.setSelectedLayer(null);
    					stb.setSelectedLayers(null);
  					} catch(Exception e) {}
				}

				else if(arg == "Legend Editor")
				{
      				LayerProperties lp = new LayerProperties();
       				lp.setLegend(legend);
       				lp.setSelectedTabIndex(0);
       				lp.setVisible(true);
				}

      		}
    	}
    };

    toc.setMap(map);
    mytocadapter = new TocAdapter()
    {
      public void click(TocEvent e)
      {
    		legend = e.getLegend();
        	activeLayer = legend.getLayer();
        	stb.setSelectedLayer(activeLayer);
        	zptb.setSelectedLayer(activeLayer);
        	// get active layer index for promote and demote
        	activeLayerIndex = map.getLayerset().indexOf(activeLayer);
        	// layer indices are in order added, not toc order.
        	com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
        	hotlink.setSelectedLayers(layers);// replaces setToc from MOJ10
        	remlyritem.setEnabled(true);
        	propsitem.setEnabled(true);
      }
    };

    map.addMouseMotionListener(new MouseMotionAdapter()
    {
      	public void mouseMoved(MouseEvent me)
      	{
			com.esri.mo2.cs.geom.Point worldPoint = null;
			if (map.getLayerCount() > 0)
			{
  				worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
  				String s = "X:"+df.format(worldPoint.getX())+" "+
             				"Y:"+df.format(worldPoint.getY());
  				statusLabel.setText(s);
        	}

        	else
          		statusLabel.setText("X:0.000 Y:0.000");
      	}
    }
    );

    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    file.add(addlyritem);
    file.add(remlyritem);
    file.add(propsitem);
    mbar.add(file);
    hotlink.addPickListener(picklis);
    hotlink.setPickWidth(5);// sets tolerance for hotlink clicks
    hotjb.addActionListener(lis);
    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
    pointer.addActionListener(lis);
    pointer.setToolTipText("Return back to the pointer");
    jtb.add(hotjb);
    jtb.add(pointer);
    myjp.add(jtb);
    myjp.add(zptb);
    myjp.add(stb);
    myjp2.add(statusLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }

  private void addShapefileToMap(Layer layer,String s)
  {
    String datapath = s;
    layer.setDataset("0;" + datapath);
    map.add(layer);
  }

  public static void main(String[] args)
  {
    MyAssign qstart = new MyAssign();
    qstart.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    }
    );

    qstart.setVisible(true);
    env = map.getExtent();
  }


}// The MYAssign class closes


class Arrow extends Tool
{
  public void mouseClicked(MouseEvent me)
  {}
}
class AddLyrDialog extends JDialog
{
  Map map;
  ActionListener lis;
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JPanel panel1 = new JPanel();
  com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.
  CustomDatasetEditor();

  AddLyrDialog() throws IOException
  {
    setBounds(50,50,520,430);
    setTitle("Select a theme/layer");
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        setVisible(false);
      }
    }
    );

    lis = new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Object source = ae.getSource();
        if (source == cancel)
          setVisible(false);
        else
        {
          try {
            	setVisible(false);
            	map.getLayerset().addLayer(cus.getLayer());
            	map.redraw();
            	if (MyAssign.stb.getSelectedLayers() != null)
            	{
               	//	MyAssign.promoteitem.setEnabled(true);
               	}
  			  } catch(IOException e){}
		}
      }
    };

    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    getContentPane().add(cus,BorderLayout.CENTER);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }

  public void setMap(com.esri.mo2.ui.bean.Map map1)
  {
    map = map1;
  }
}


class HotPick extends JDialog
{
  String selected_country = MyAssign.selected_country;
  String region = MyAssign.region;
  String capital_city = MyAssign.capital_city;
  String continent = null;
  String flagpic = null;
  String curr = null;

  JPanel goes_south = new JPanel();
  JPanel goes_center = new JPanel();
  JPanel goes_west = new JPanel();

  String[][]countries=
  	{
  		{"India",			"Asia",			"indiaflag.gif",		"Rupees"},
  		{"China",			"Asia",			"chinaflag.jpg",		"Yuan"},
    	{"Japan",			"Asia",			"japanflag.jpg",		"Yen"},
    	{"Iran",			"Asia",			"iranflag.gif",			"Rial"},
    	{"Saudi Arabia",	"Asia",			"saudiflag.gif",		"Riyal"},
    	{"Russia",			"Europe",		"russiaflag.gif",		"Ruble"},
    	{"France",			"Europe",		"franceflag.jpg",		"Euro"},
    	{"Spain",			"Europe",		"spainflag.gif",		"Euro"},
    	{"Italy",			"Europe",		"italyflag.jpg",		"Euro"},
    	{"United Kingdom",	"Europe",		"ukflag.jpg",			"Pound sterling"},
    	{"United States",	"North America","usaflag.gif",			"Dollar"},
    	{"Canada",			"North America","canadaflag.gif",		"Canadian dollar"},
    	{"Mexico",			"North America","mexicoflag.gif",		"Mexican peso"},
    	{"Brazil",			"South America","brazilflag.gif",		"Real"},
    	{"Colombia",		"South America","colombiaflag.jpg",		"Colombian Peso"},
    	{"Argentina",		"South America","argentinaflag.jpg",	"Peso"},
    	{"Peru",			"South America","peruflag.jpg",			"Nuevo sol"},
    	{"Ecuador",			"South America","ecuadorflag.jpg",		"U.S. dollar"},
    	{"South Africa",	"Africa",		"southafricaflag.jpg",	"Rand"},
    	{"Egypt",			"Africa",		"egyptflag.jpg",		"Egyptian pound"},
    	{"Ethiopia",		"Africa",		"ethiopiaflag.jpg",		"Birr"},
    	{"Zimbabwe",		"Africa",		"zimbabweflag.jpg",		"Zimbabwean dollar"},
    	{"Australia",		"Australia",	"australiaflag.jpg",	"Australian dollar"},
    	{"New Zealand",		"Australia",	"newzealandflag.jpg",	"New Zealand dollar"},
    };
  HotPick() throws IOException
  {
	setTitle("Country Flag");
    setBounds(50,50,1000,500);

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
    		setVisible(false);
  	  }
    }
    );
    for (int i=0 ; i<24 ; i++)
    {
  		if (countries[i][0].equals(selected_country))
  		{
        	continent = countries[i][2];
    		flagpic = countries[i][2];
    		curr = countries[i][3];
    		break;
  		}
    }


    	Font labelfont = new Font("",1,24);
    	Font f = new Font("",1,30);

    	JLabel stars = new JLabel("****************" + selected_country + "*****************");

    	JLabel label_welcome = new JLabel("WELCOME TO "+ selected_country);
    	JLabel label_curr = new JLabel("CURRENCY: "+curr);
    	JLabel label_region = new JLabel("REGION: "+ region);
    	JLabel label_capital = new JLabel("CAPITAL: "+ capital_city);


    	stars.setFont(f);
    	label_welcome.setFont(labelfont);
    	label_curr.setFont(labelfont);
    	label_region.setFont(labelfont);
    	label_capital.setFont(labelfont);


    	ImageIcon flagIcon = new ImageIcon(flagpic);
    	JLabel flagLabel = new JLabel(flagIcon);

		goes_west.setLayout(new GridLayout(8,1));

		goes_west.add(label_welcome);
    	goes_west.add(label_curr);
    	goes_west.add(label_region);
    	goes_west.add(label_capital);

    	goes_west.setBackground(Color.WHITE);
    	goes_south.setBackground(Color.CYAN);



    	goes_center.add(flagLabel);

    	goes_south.add(stars);

    	getContentPane().add(goes_center,BorderLayout.CENTER);
    	getContentPane().add(goes_south,BorderLayout.SOUTH);
    	getContentPane().add(goes_west,BorderLayout.WEST);

    	System.out.println("Country:" + selected_country);
    	System.out.println("Continent:"+ continent);
    	System.out.println("Currency:"+ curr+ "\n");

  }
}
