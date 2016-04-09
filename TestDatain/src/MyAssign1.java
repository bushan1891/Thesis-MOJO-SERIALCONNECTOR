import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.data.feat.Feature;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.draw.AoFillStyle;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.SimplePolygonSymbol;
import com.esri.mo2.ui.bean.Identify;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Legend;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.bean.PickEvent;
import com.esri.mo2.ui.bean.PickListener;
import com.esri.mo2.ui.bean.Toc;
import com.esri.mo2.ui.bean.TocAdapter;
import com.esri.mo2.ui.bean.TocEvent;
import com.esri.mo2.ui.bean.Tool;
import com.esri.mo2.ui.dlg.AboutBox;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

public class MyAssign1 extends JFrame {
	protected static final Frame frame = null;
	static Map map = new Map();
	static boolean fullMap = true;
	Legend legend;
	Legend legend2;
	Legend legend3;
	Layer layer = new Layer();
	Layer layer2 = new Layer();
	Layer layer3 = new Layer();
	private ArrayList helpText = new ArrayList(3);

	static com.esri.mo2.map.dpy.Layer layer4;
	com.esri.mo2.map.dpy.Layer activeLayer;
	int activeLayerIndex;
	JMenuBar mbar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenuItem addlyritem = new JMenuItem("add layer", new ImageIcon(
			"res/iconsaddtheme.gif"));
	JMenuItem remlyritem = new JMenuItem("remove layer", new ImageIcon(
			"res/icons/delete.gif"));
	JMenuItem propsitem = new JMenuItem("Legend Editor", new ImageIcon(
			"res/icons/properties.gif"));
	JMenu help = new JMenu("Help");
	JMenuItem help1 = new JMenuItem("Help");
	JMenuItem about = new JMenuItem("About");
	JMenuItem contact = new JMenuItem("Contact Us");
	Toc toc = new Toc();
	// String s1 = "F:\\esri\\MOJ20\\Samples\\Data\\World\\country.shp";
	// String s2 = "F:\\esri\\MOJ20\\Samples\\Data\\World\\MyShapeFile.shp";
	String s1 = "res\\CaliforniaCounty.shp";
	String s2 = "res\\MedicalSchools.shp";

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
	JButton pointer = new JButton(new ImageIcon("res/icons/pointer.gif"));
	JButton hotjb = new JButton(new ImageIcon("res/hotlink.gif"));
	JButton gesture = new JButton(new ImageIcon("res/gesture2.gif"));
	JButton settings = new JButton(new ImageIcon("res/icons/setting1.gif"));
	JButton closeGesture = new JButton(new ImageIcon("res/icons/close.gif"));
	Arrow arrow = new Arrow();
	ActionListener lis;
	ActionListener layerlis;
	ActionListener helplis;
	TocAdapter mytocadapter;
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("res/hotlink.gif"); // 16x16 gif file
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt, new Point(6, 30),
			"bolt");
	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify(); // the Identify class implements a
										// PickListener,
	Integer fieldLevel = 0; // for field of operation value
	static String selected_country = null;
	static String region = null;
	static String capital_city = null;
	static String school = null;
	static String des = null;
	static String picture = null;
	static String link = null;

	class MyPickAdapter implements PickListener { // implements hotlink
		public void beginPick(PickEvent pe) {
		};

		// this fires even when you click outside the states layer
		public void endPick(PickEvent pe) {
		}

		public void foundData(PickEvent pe) {
			// fires only when a layer feature is clicked
			FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
			com.esri.mo2.data.feat.Cursor c = pe.getCursor();
			Feature f = null;
			Fields fields = null;

			if (c != null)
				f = (Feature) c.next();

			fields = f.getFields();
			System.out.println("****" + fields);

			selected_country = (String) f.getValue(5);
			region = (String) f.getValue(5);
			capital_city = (String) f.getValue(7);

			System.out.println("Checkpoint :: :: ::" + selected_country);
			try {
				HotPick hotpick = new HotPick();// opens dialog window with Duke
												// in it
				hotpick.setVisible(true);
			} catch (Exception e) {
			}
		}
	};

	static Envelope env;

	public MyAssign1() {
		super("My Assignment");
		this.setSize(1000, 500);
		zptb.setMap(map);
		stb.setMap(map);
		setJMenuBar(mbar);

		ActionListener lisZoom = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = false;
			}
		}; // can change a boolean here

		ActionListener lisFullExt = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = true;
			}
		};
		// next line gets ahold of a reference to the zoomin button
		JButton zoomInButton = (JButton) zptb.getActionComponent("ZoomIn");
		JButton zoomFullExtentButton = (JButton) zptb
				.getActionComponent("ZoomToFullExtent");
		JButton zoomToSelectedLayerButton = (JButton) zptb
				.getActionComponent("ZoomToSelectedLayer");
		zoomInButton.addActionListener(lisZoom);
		zoomFullExtentButton.addActionListener(lisFullExt);
		zoomToSelectedLayerButton.addActionListener(lisZoom);

		complistener = new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				if (fullMap) {
					map.setExtent(env);
					map.zoom(1.0); // scale is scale factor in pixels
					map.redraw();
				}
			}
		};
		addComponentListener(complistener);
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();

				if (source == hotjb) {
					hotlink.setCursor(boltCursor);
					map.setSelectedTool(hotlink);
				} else if (source == pointer) {
					map.setSelectedTool(arrow);
				}

				else if (source == gesture) {
					System.out.println("i am the gesture listner");
					try {
						SerialTest.test(map, env, fieldLevel);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					map.setSelectedTool(hotlink);
				}

				else if (source == settings) {

					// Custom button text
					System.out.println("i was called");

					Object[] options = { "Level 1", "Level 2", "Level 3" };
					fieldLevel = JOptionPane.showOptionDialog(frame,
							"Please Set the Field of Operation ",
							"A Silly Question",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[2]);
					System.out.println("value of n is " + fieldLevel);

					try {
						SerialTest.close();
						SerialTest.test(map, env, fieldLevel);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				else if (source==closeGesture) {
					try {
						SerialTest.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("connection colsed");
				}

			}
		};

		layerlis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if (arg == "add layer") {
						try {
							AddLyrDialog aldlg = new AddLyrDialog();
							aldlg.setMap(map);
							aldlg.setVisible(true);
						} catch (IOException e) {
						}
					}

					else if (arg == "remove layer") {
						try {
							com.esri.mo2.map.dpy.Layer dpylayer = legend
									.getLayer();
							map.getLayerset().removeLayer(dpylayer);
							map.redraw();
							remlyritem.setEnabled(false);
							propsitem.setEnabled(false);
							stb.setSelectedLayer(null);
							zptb.setSelectedLayer(null);
							stb.setSelectedLayers(null);
						} catch (Exception e) {
						}
					}

					else if (arg == "Legend Editor") {
						LayerProperties lp = new LayerProperties();
						lp.setLegend(legend);
						lp.setSelectedTabIndex(0);
						lp.setVisible(true);
					}

				}
			}
		};
		helplis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if (arg == "About") {
						AboutBox aboutbox = new AboutBox();
						aboutbox.setProductName("MOJO");
						aboutbox.setProductVersion("2.0");
						aboutbox.setVisible(true);
					} else if (arg == "Contact Us") {
						try {
							String s = "\n\n  For any queries please contact on : \n\n   email:sayali.utekar@gmail.com \n\n   phone:6197045555";
							HelpDialog helpdialog = new HelpDialog(s);
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Help") {
						try {
							HelpDialog helpdialog = new HelpDialog(
									(String) helpText.get(0));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					}
				}
			}
		};

		toc.setMap(map);
		mytocadapter = new TocAdapter() {
			public void click(TocEvent e) {
				legend = e.getLegend();
				activeLayer = legend.getLayer();
				stb.setSelectedLayer(activeLayer);
				zptb.setSelectedLayer(activeLayer);
				// get active layer index for promote and demote
				activeLayerIndex = map.getLayerset().indexOf(activeLayer);
				// layer indices are in order added, not toc order.
				com.esri.mo2.map.dpy.Layer[] layers = { activeLayer };
				hotlink.setSelectedLayers(layers);// replaces setToc from MOJ10
				remlyritem.setEnabled(true);
				propsitem.setEnabled(true);
			}
		};

		map.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me) {
				com.esri.mo2.cs.geom.Point worldPoint = null;
				if (map.getLayerCount() > 0) {
					worldPoint = map.transformPixelToWorld(me.getX(), me.getY());
					String s = "X:" + df.format(worldPoint.getX()) + " " + "Y:"
							+ df.format(worldPoint.getY());
					statusLabel.setText(s);
				}

				else
					statusLabel.setText("X:0.000 Y:0.000");
			}
		});
		// toc.setBackground(Color.darkGray);
		toc.addTocListener(mytocadapter);
		remlyritem.setEnabled(false); // assume no layer initially selected
		propsitem.setEnabled(false);
		addlyritem.addActionListener(layerlis);
		remlyritem.addActionListener(layerlis);
		propsitem.addActionListener(layerlis);
		file.add(addlyritem);
		file.add(remlyritem);
		file.add(propsitem);
		help1.addActionListener(helplis);
		about.addActionListener(helplis);
		contact.addActionListener(helplis);
		help.add(help1);
		help.add(about);
		help.add(contact);
		mbar.add(file);
		mbar.add(help);
		map.setBackground(Color.getHSBColor(40, 40, 10));
		hotlink.addPickListener(picklis);
		hotlink.setPickWidth(5);// sets tolerance for hotlink clicks
		hotjb.addActionListener(lis);
		gesture.addActionListener(lis);
		settings.addActionListener(lis);
		closeGesture.addActionListener(lis);

		/* will have to write a lister to this */
		// settings.addActionListener();
		settings.setToolTipText("Click Change Gesture Setting");
		gesture.setToolTipText("Click to Enter Gesture Mode");
		closeGesture.setToolTipText("Click to close the connection");
		
		hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
		pointer.addActionListener(lis);
		pointer.setToolTipText("Return back to the pointer");
		// pointer.setBackground(Color.BLUE);

		jtb.add(closeGesture);
		jtb.add(settings);
		jtb.add(gesture);
		jtb.add(hotjb);
		jtb.add(pointer);
		// jtb.setBackground(Color.BLACK);
		myjp.add(jtb);
		myjp.add(zptb);
		myjp.add(stb);
		myjp2.add(statusLabel);
		// myjp2.setBackground(Color.BLACK);
		setuphelpText();
		getContentPane().add(map, BorderLayout.CENTER);
		getContentPane().add(myjp, BorderLayout.NORTH);
		getContentPane().add(myjp2, BorderLayout.SOUTH);
		addShapefileToMap(layer, s1);
		addShapefileToMap(layer2, s2);

		java.util.List list = toc.getAllLegends();
		com.esri.mo2.map.dpy.Layer lay1 = ((Legend) list.get(1)).getLayer(); // states
																				// layer
		com.esri.mo2.map.dpy.Layer lay0 = ((Legend) list.get(0)).getLayer();
		FeatureLayer flayer1 = (FeatureLayer) lay1;
		FeatureLayer flayer0 = (FeatureLayer) lay0;
		BaseSimpleRenderer bsr1 = (BaseSimpleRenderer) flayer1.getRenderer();
		BaseSimpleRenderer bsr0 = (BaseSimpleRenderer) flayer0.getRenderer();
		SimplePolygonSymbol sym1 = (SimplePolygonSymbol) bsr1.getSymbol();
		SimpleMarkerSymbol sym0 = (SimpleMarkerSymbol) bsr0.getSymbol();
		sym1.setPaint(AoFillStyle.getPaint(
				com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,
				new java.awt.Color(0, 150, 15)));
		sym0.setSymbolColor(java.awt.Color.red);
		bsr1.setSymbol(sym1);
		bsr0.setSymbol(sym0);

		getContentPane().add(toc, BorderLayout.WEST);
	}

	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;" + datapath);
		map.add(layer);
	}

	private void setuphelpText() {
		String s0 = "Medical Schools in California\n\nThis project will show all the major medical schools in California.\n"
				+ "The map displayed will be California state map.\n"
				+ "All the Medical schools in California are shown using Red points.\n"
				+ "If we click on any of the feature i.e the county/city in this case, \n"
				+ "it will pop a new window and show details of the medical school.";

		helpText.add(s0);
		String s1 = "  The Legend Editor is a menu item found under the File menu. \n"
				+ "    Given that a layer is selected by clicking on its legend in the table of \n"
				+ "    contents, clicking on Legend Editor will open a window giving you choices \n"
				+ "    about how to display that layer.  For example you can control the color \n"
				+ "    used to display the layer on the map, or whether to use multiple colors ";
		helpText.add(s1);
		String s2 = "  Layer Control is a Menu on the menu bar.  If you have selected a \n"
				+ " layer by clicking on a legend in the toc (table of contents) to the left of \n"
				+ " the map, then the promote and demote tools will become usable.  Clicking on \n"
				+ " promote will raise the selected legend one position higher in the toc, and \n"
				+ " clicking on demote will lower that legend one position in the toc.";
		helpText.add(s2);
		String s3 = "    This tool will allow you to learn about certain other tools. \n"
				+ "    You begin with a standard left mouse button click on the Help Tool itself.\n"
				+ "    RIGHT click on another tool and a window may give you information about the \n"
				+ "    intended usage of the tool.  Click on the arrow tool to stop using the \n"
				+ "    help tool.";
		helpText.add(s3);
		String s4 = "If you click on the Zoom In tool, and then click on the map, you \n"
				+ " will see a part of the map in greater detail.  You can zoom in multiple times.\n"
				+ " You can also sketch a rectangular part of the map, and zoom to that.  You can\n"
				+ " undo a Zoom In with a Zoom Out or with a Zoom to Full Extent";
		helpText.add(s4);
		String s5 = "You must have a selected layer to use the Zoom to Active Layer tool.\n"
				+ "    If you then click on Zoom to Active Layer, you will be shown enough of \n"
				+ "    the full map to see all of the features in the layer you select.  If you \n"
				+ "    select a layer that shows where glaciers are, then you do not need to \n"
				+ "    see Hawaii, or any southern states, so you will see Alaska, and northern \n"
				+ "    mainland states.";
		helpText.add(s5);

	}

	public static void main(String[] args) {
		MyAssign1 qstart = new MyAssign1();
		qstart.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		qstart.setVisible(true);
		env = map.getExtent();
	}

}// The MYAssign class closes

class Arrow extends Tool {
	public void mouseClicked(MouseEvent me) {
	}
}

class AddLyrDialog extends JDialog {
	Map map;
	ActionListener lis;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JPanel panel1 = new JPanel();
	com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.CustomDatasetEditor();

	AddLyrDialog() throws IOException {
		setBounds(50, 50, 520, 430);
		setTitle("Select a theme/layer");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == cancel)
					setVisible(false);
				else {
					try {
						setVisible(false);
						map.getLayerset().addLayer(cus.getLayer());
						map.redraw();
						if (MyAssign1.stb.getSelectedLayers() != null) {
							// MyAssign1.promoteitem.setEnabled(true);
						}
					} catch (IOException e) {
					}
				}
			}
		};

		ok.addActionListener(lis);
		cancel.addActionListener(lis);
		getContentPane().add(cus, BorderLayout.CENTER);
		panel1.add(ok);
		panel1.add(cancel);
		getContentPane().add(panel1, BorderLayout.SOUTH);
	}

	public void setMap(com.esri.mo2.ui.bean.Map map1) {
		map = map1;
	}
}

class HotPick extends JDialog {
	String selected_country = MyAssign1.selected_country;
	String region = MyAssign1.region;
	String capital_city = MyAssign1.capital_city;
	String school = MyAssign1.school;
	String des = MyAssign1.des;
	String picture = MyAssign1.picture;
	String link = MyAssign1.link;
	JPanel goes_north = new JPanel();
	JPanel goes_south = new JPanel();
	JPanel goes_center = new JPanel();
	JPanel goes_west = new JPanel();

	String[][] countries = {
			{ "San Diego", "1",
					"University of Southern California College of Medicine",
					"ucsd.jpg", "http://som.ucsd.edu/", },
			{ "Riverside", "2", "UC Riverside School of Medicine",
					"Riverside.jpg",
					"http://medschool.ucr.edu/about/where.html" },
			{ "San Bernardino", "3",
					"Loma Linda University School of Medicine",
					"Loma-Linda-University-Medical-Center-656x350.jpg",
					"http://www.llu.edu/medicine/index.page" },
			{ "Orange", "7",
					"University of California, Irvine School of Medicine",
					"irvine.jpg", "http://www.som.uci.edu/" },
			{
					"Los Angeles",
					"10",
					"Keck School of Medicine of University of Southern California",
					"USCKeckSchoolofMedicine.jpg", "http://keck.usc.edu/" },
			{ "San Francisco", "5",
					"University of California – San Francisco",
					"UCSF_0899.jpg", "http://medschool.ucsf.edu/" },
			{ "Santa Clara", "7", "Stanford University School of Medicine",
					"stanford.jpg", "http://med.stanford.edu/" },
			{
					"Solano",
					"33",
					"Touro University California College of Osteopathic Medicine",
					"touro.jpg", "http://com.tu.edu/" },
			{ "Sacramento", "22", "UC Davis School of Medicine", "davis.jpg",
					"http://www.ucdmc.ucdavis.edu/medschool/" },
			{
					"Santa Barbara",
					"3",
					"University of California, Santa Barbara",
					"SantaBarbara.jpg",
					"http://my.sa.ucsb.edu/catalog/2013-2014/CollegesDepartments/ls-intro/Medicine.aspx" },

	};

	HotPick() throws IOException {
		setTitle("Medical School");
		setBounds(50, 50, 1000, 500);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		for (int i = 0; i < 24; i++) {
			System.out.println("Hello " + selected_country);
			if (countries[i][0].equals(selected_country)) {
				des = countries[i][1];
				school = countries[i][2];
				picture = countries[i][3];
				link = countries[i][4];
				break;
			}
		}

		Font labelfont = new Font("", 1, 15);
		Font f = new Font("Comic San MS", 1, 15);

		JLabel stars = new JLabel("****************" + school
				+ "*****************");

		// JLabel label_welcome = new JLabel("WELCOME TO "+ school);
		JLabel label_name = new JLabel("Name: " + school);
		JLabel label_des = new JLabel("Rank: " + des);
		JLabel label_region = new JLabel("Location: " + selected_country);
		JLabel label_link = new JLabel("Website: " + link);

		// JLabel label_capital = new JLabel(": "+ capital_city);

		stars.setFont(f);
		// label_welcome.setFont(labelfont);
		label_name.setFont(labelfont);
		label_des.setFont(labelfont);
		label_link.setFont(labelfont);
		label_region.setFont(labelfont);
		// label_capital.setFont(labelfont);

		ImageIcon flagIcon = new ImageIcon(
				((new ImageIcon(picture)).getImage()).getScaledInstance(400,
						300, java.awt.Image.SCALE_SMOOTH));
		JLabel flagLabel = new JLabel(flagIcon);
		goes_north.add(flagLabel);
		goes_west.setLayout(new GridLayout(8, 1));

		// goes_west.add(label_welcome);

		goes_west.add(label_name);
		goes_west.add(label_des);
		goes_west.add(label_region);
		goes_west.add(label_link);
		// goes_west.add(label_capital);

		goes_west.setBackground(Color.WHITE);
		goes_south.setBackground(Color.GRAY);

		goes_south.add(stars);
		getContentPane().add(goes_north, BorderLayout.NORTH);
		// getContentPane().add(goes_center,BorderLayout.CENTER);
		getContentPane().add(goes_south, BorderLayout.SOUTH);
		getContentPane().add(goes_west, BorderLayout.CENTER);

		System.out.println("Country:" + selected_country);
		System.out.println("Continent:" + school);
		System.out.println("Currency:" + link + "\n");

	}
}

class HelpDialog extends JDialog {
	JTextArea helptextarea;

	public HelpDialog(String inputText) throws IOException {
		setBounds(70, 70, 460, 250);
		setTitle("Help");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		Font f2 = new Font("Comic San MS", 1, 15);

		helptextarea = new JTextArea(inputText, 7, 40);
		helptextarea.setFont(f2);
		JScrollPane scrollpane = new JScrollPane(helptextarea);
		helptextarea.setEditable(false);
		getContentPane().add(scrollpane, "Center");
	}
}
