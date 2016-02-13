//hotlink images//
import java.awt.MediaTracker;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import  java.awt.*;
import  java.awt.event.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,Tool
// TocEvent,Legend(a legend is part of a toc),ActateLayer
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import  com.esri.mo2.map.dpy.FeatureLayer;
import  com.esri.mo2.map.dpy.BaseFeatureLayer;
import  com.esri.mo2.map.draw.SimpleMarkerSymbol;
import  com.esri.mo2.map.draw.TrueTypeMarkerSymbol;
import  com.esri.mo2.map.draw.BaseSimpleRenderer;
import  com.esri.mo2.file.shp.*;
import  com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.awt.geom.*;
import com.esri.mo2.cs.geom.*; //using Envelope, Point, BasePointsArray

public class Project extends JFrame
{
static Map map = new Map();
static boolean fullMap = true; // Map not zoomed

Legend legend;
Legend legend2;

Layer layer1 = new Layer();
Layer layer2 = new Layer();
Layer layer3 = null;

static AcetateLayer acetLayer;
static com.esri.mo2.map.dpy.Layer layer4;

com.esri.mo2.map.dpy.Layer activeLayer;
int activeLayerIndex;

com.esri.mo2.cs.geom.Point initPoint,endPoint;
double distance;

JMenuBar mbar = new JMenuBar();

JMenu file = new JMenu("File");
JMenu theme = new JMenu("Theme");
JMenu layercontrol = new JMenu("LayerControl");

JMenuItem attribitem = new JMenuItem("open attribute table",new ImageIcon("tableview"));
JMenuItem createlayeritem = new JMenuItem("create layer from selection",new ImageIcon("Icon0915b.jpg"));
static JMenuItem promoteitem = new JMenuItem("promote selected layer",new ImageIcon("promote.jpg"));
JMenuItem demoteitem = new JMenuItem("demote selected layer",new ImageIcon("demote.jpg"));
JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));

Toc toc = new Toc();
String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\states.shp";
String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
String datapathname = "";
String legendname = "";

ZoomPanToolBar zptb = new ZoomPanToolBar();
static SelectionToolBar stb = new SelectionToolBar();
JToolBar jtb = new JToolBar();

ComponentListener complistener;

JLabel statusLabel = new JLabel("status bar LOC");
static JLabel milesLabel = new JLabel(" DIST: 0 mi ");
static JLabel kmLabel = new JLabel(" 0 km ");
java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");

JPanel myjp = new JPanel();
JPanel myjp2 = new JPanel();

JButton prtjb = new JButton(new ImageIcon("print.gif"));
JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
JButton ptrjb = new JButton(new ImageIcon("pointer.gif"));
JButton distjb = new JButton(new ImageIcon("measure_1.gif"));
JButton XYjb = new JButton("XY");
JButton hotjb=new JButton(new ImageIcon("hotlink.jpg"));

static Arrow arrow = new Arrow();

DistanceTool distanceTool= new DistanceTool();

ActionListener lis;
ActionListener layerlis;
ActionListener layercontrollis;
TocAdapter mytocadapter;
static Envelope env;



public Project()
{
super("Project ");
// distanceTool.setMeasureUnit(com.esri.mo2.util.Units.MILES);
// map.setMapUnit(com.esri.mo2.util.Units.MILES);
this.setSize(700,450);

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
JButton zoomFullExtentButton =(JButton)zptb.getActionComponent("ZoomToFullExtent");
JButton zoomToSelectedLayerButton =(JButton)zptb.getActionComponent("ZoomToSelectedLayer");

zoomInButton.addActionListener(lisZoom);
zoomFullExtentButton.addActionListener(lisFullExt);
zoomToSelectedLayerButton.addActionListener(lisZoom);

hotjb.addActionListener(lis);
hotjb.setToolTipText("hotlink-click on a shappe to see a picture");
jtb.add(hotjb);


complistener = new ComponentAdapter ()
{
public void componentResized(ComponentEvent ce)
{
if(fullMap)
{
map.setExtent(env);
map.zoom(1.0); //scale is scale factor in pixels
map.redraw();
}
}
};

addComponentListener(complistener);
ActionListener lis = new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
Object source = ae.getSource();
if (source == prtjb || source instanceof JMenuItem )
{
com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
mapPrint.setMap(map);
mapPrint.doPrint();// prints the map
}
else if (source == ptrjb)
{
//Arrow arrow = new Arrow();
map.setSelectedTool(arrow);
}
else if (source == distjb)
{
DistanceTool distanceTool = new DistanceTool();
map.setSelectedTool(distanceTool);
}
else if(source==hotjb)
{
Hotlink hotlink = new Hotlink();

map.setSelectedTool(hotlink);

}
else if (source == XYjb)
{
try
{
AddXYtheme addXYtheme = new AddXYtheme();
addXYtheme.setMap(map);
addXYtheme.setVisible(false);
map.redraw();
}
catch (IOException e){}
}
else
{
try
{
AddLyrDialog aldlg = new AddLyrDialog();
aldlg.setMap(map);
aldlg.setVisible(true);
}
catch(IOException e){}
}
}
};

layercontrollis = new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
String source = ae.getActionCommand();
System.out.println(activeLayerIndex+" active index");

if (source == "promote selected layer")
map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);

else
map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
enableDisableButtons();
map.redraw();
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
try
{
AddLyrDialog aldlg = new AddLyrDialog();
aldlg.setMap(map);
aldlg.setVisible(true);
}
catch(IOException e){}
}
else if(arg == "remove layer")
{
try
{
com.esri.mo2.map.dpy.Layer dpylayer =legend.getLayer();
map.getLayerset().removeLayer(dpylayer);
map.redraw();
remlyritem.setEnabled(false);
propsitem.setEnabled(false);
attribitem.setEnabled(false);
promoteitem.setEnabled(false);
demoteitem.setEnabled(false);
stb.setSelectedLayer(null);
}
catch(Exception e) {}
}
else if(arg == "Legend Editor")
{
LayerProperties lp = new LayerProperties();
lp.setLegend(legend);
lp.setSelectedTabIndex(0);
lp.setVisible(true);
}
else if (arg == "open attribute table")
{
try
{
layer4 = legend.getLayer();
AttrTab attrtab = new AttrTab();
attrtab.setVisible(true);
}
catch(IOException ioe){}
}
else if (arg=="create layer from selection")
{
com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new com.esri.mo2.map.draw.BaseSimpleRenderer();
com.esri.mo2.map.draw.SimpleFillSymbol sfs = new com.esri.mo2.map.draw.SimpleFillSymbol();// for polygons
sfs.setSymbolColor(new Color(255,255,0)); // mellow yellow
sfs.setType(com.esri.mo2.map.draw.SimpleFillSymbol.FILLTYPE_SOLID);
sfs.setBoundary(true);
layer4 = legend.getLayer();
FeatureLayer flayer2 = (FeatureLayer)layer4;
// select, e.g., Montana and then click the
// create layer menuitem; next line verifies a selection was made
System.out.println("has selected" + flayer2.hasSelection());
//next line creates the 'set' of selections
if (flayer2.hasSelection())
{
SelectionSet selectset = flayer2.getSelectionSet();
// next line makes a new feature layer of the selections
FeatureLayer selectedlayer =flayer2.createSelectionLayer(selectset);
sbr.setLayer(selectedlayer);
sbr.setSymbol(sfs);
selectedlayer.setRenderer(sbr);
Layerset layerset = map.getLayerset();
// next line places a new visible layer, e.g. Montana, on the map
layerset.addLayer(selectedlayer);
selectedlayer.setVisible(true);

if(stb.getSelectedLayers() != null)
promoteitem.setEnabled(true);
try
{
legend2 = toc.findLegend(selectedlayer);
}
catch (Exception e) {}

CreateShapeDialog csd = new CreateShapeDialog(selectedlayer);
csd.setVisible(true);
Flash flash = new Flash(legend2);
flash.start();
map.redraw(); // necessary to see color immediately
}
}
}
}
};
toc.setMap(map);

mytocadapter = new TocAdapter()
{
public void click(TocEvent e)
{
System.out.println(activeLayerIndex+ "dex");
legend = e.getLegend();
activeLayer = legend.getLayer();
stb.setSelectedLayer(activeLayer);
zptb.setSelectedLayer(activeLayer);
// get acive layer index for promote and demote
activeLayerIndex = map.getLayerset().indexOf(activeLayer);
// layer indices are in order added, not toc order.
System.out.println(activeLayerIndex + "active ndex");
remlyritem.setEnabled(true);
propsitem.setEnabled(true);
attribitem.setEnabled(true);
enableDisableButtons();
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
String s = "X:"+df.format(worldPoint.getX())+" "+"Y:"+df.format(worldPoint.getY());
statusLabel.setText(s);
}
else
statusLabel.setText("X:0.000 Y:0.000");
}
});

toc.addTocListener(mytocadapter);

remlyritem.setEnabled(false); // assume no layer initially selected
propsitem.setEnabled(false);
attribitem.setEnabled(false);

promoteitem.setEnabled(false);
demoteitem.setEnabled(false);

printitem.addActionListener(lis);

addlyritem.addActionListener(layerlis);
remlyritem.addActionListener(layerlis);

propsitem.addActionListener(layerlis);
attribitem.addActionListener(layerlis);

createlayeritem.addActionListener(layerlis);
promoteitem.addActionListener(layercontrollis);
demoteitem.addActionListener(layercontrollis);

file.add(addlyritem);
file.add(printitem);
file.add(remlyritem);
file.add(propsitem);

theme.add(attribitem);
theme.add(createlayeritem);

layercontrol.add(promoteitem);
layercontrol.add(demoteitem);

mbar.add(file);
mbar.add(theme);
mbar.add(layercontrol);

prtjb.addActionListener(lis);
prtjb.setToolTipText("print map");

addlyrjb.addActionListener(lis);
addlyrjb.setToolTipText("add layer");

ptrjb.addActionListener(lis);
distjb.addActionListener(lis);

XYjb.addActionListener(lis);
XYjb.setToolTipText("add a layer of points from a file");

prtjb.setToolTipText("pointer");
distjb.setToolTipText("press-drag-release to measure a distance");

jtb.add(prtjb);
jtb.add(addlyrjb);
jtb.add(ptrjb);
jtb.add(distjb);
jtb.add(XYjb);

myjp.add(jtb);
myjp.add(zptb); myjp.add(stb);
myjp2.add(statusLabel);
myjp2.add(milesLabel);myjp2.add(kmLabel);

getContentPane().add(map, BorderLayout.CENTER);
getContentPane().add(myjp,BorderLayout.NORTH);
getContentPane().add(myjp2,BorderLayout.SOUTH);

addShapefileToMap(layer1,s1);
addShapefileToMap(layer2,s2);
getContentPane().add(toc, BorderLayout.WEST);
}

private void addShapefileToMap(Layer layer,String s)
{
String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
layer.setDataset("0;"+datapath);
map.add(layer);
}

public static void main(String[] args)
{
Project assign = new Project();
assign.addWindowListener(new WindowAdapter()
{
public void windowClosing(WindowEvent e)
{
System.out.println("Thanks, Quick Start exits");
System.exit(0);
}
});
assign.setVisible(true);
env = map.getExtent();
}

private void enableDisableButtons()
{
int layerCount = map.getLayerset().getSize();
if (layerCount < 2)
{
promoteitem.setEnabled(false);
demoteitem.setEnabled(false);
}
else if (activeLayerIndex == 0)
{
demoteitem.setEnabled(false);
promoteitem.setEnabled(true);
}
else if (activeLayerIndex == layerCount - 1)
{
promoteitem.setEnabled(false);
demoteitem.setEnabled(true);
}
else
{
promoteitem.setEnabled(true);
demoteitem.setEnabled(true);
}
}
}
// following is an Add Layer dialog window

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
});
lis = new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
Object source = ae.getSource();
if (source == cancel)
setVisible(false);
else
{
try
{
setVisible(false);
map.getLayerset().addLayer(cus.getLayer());
map.redraw();
// if (Project.stb.getSelectedLayer() != null)
// Project.promoteitem.setEnabled(true);
}
catch(IOException e){}
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

class AddXYtheme extends JDialog
{
Map map;
JFileChooser jfc = new JFileChooser();
BasePointsArray bpa = new BasePointsArray();
java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
String address[] = new String[20];
String address1[] = new String[600];

AddXYtheme() throws IOException
{
setBounds(50,50,520,430);
File dirInit = new File("C:\\ESRI\\MOJ20\\examples");
jfc.setCurrentDirectory(dirInit);//initial folder
jfc.showOpenDialog(this);

try
{
File file = jfc.getSelectedFile();
FileReader fred = new FileReader(file);
BufferedReader in = new BufferedReader(fred);
String s; // = in.readLine();

double x,y;
String sArr;
String sArr1;
int n = 0;
int i=0;
//int j=0;
while ((s = in.readLine()) != null)
{
StringTokenizer st = new StringTokenizer(s,",");
x = Double.parseDouble(st.nextToken());
y = Double.parseDouble(st.nextToken());

// System.out.println("st" + st.nextToken());
sArr = st.nextToken();
System.out.println("st" + sArr);
address[i]= sArr;
//i++;
sArr1 = st.nextToken();
address1[i]= sArr1;
i++;
bpa.insertPoint(n++,new com.esri.mo2.cs.geom.Point(x,y));
//System.out.println("Address: "+x+y+":"+address[i]);


}
}
catch (IOException e){}

XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,address,address1);
xyfl.setVisible(true);

map = Project.map;
map.getLayerset().addLayer(xyfl);
map.redraw();

map.addMouseListener(new MouseAdapter()
{
public void mouseClicked(MouseEvent me)
{
String x1,y1;
double x2, y2;
double x3, y3;
int flag = 0;
java.awt.GridBagConstraints gridBagConstraints;
com.esri.mo2.cs.geom.Point worldPoint = null;
// if (map.getLayerCount() > 0) {
worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
x1= df.format(worldPoint.getX());
x2= Math.round(worldPoint.getX()*100.0)/100.0;
y1 = df.format(worldPoint.getY());
y2= Math.round(worldPoint.getY()*100.0)/100.0;
// String s = "X1:"+ df.format(worldPoint.getX())+" "+
// "Y1:"+ df.format(worldPoint.getY());
// System.out.println("string: "+s);

JFrame frame;

JLabel l1 = new JLabel("RESTAURANTS IN SAN DIEGO");
JLabel l2 = new JLabel("X: ");

JLabel x1label,x2label,addr;

JLabel l3 = new JLabel("Y: ");
JLabel l4 = new JLabel("Address: ");

JPanel pane1 = new JPanel();
JPanel pane2 = new JPanel();

Color c = new Color(220,213,183);
Color c1= new Color(236,232,216);

for(int i=0;i<bpa.size();i++)
{
com.esri.mo2.cs.geom.Point p = new com.esri.mo2.cs.geom.Point(bpa.getPoint(i));

x1= df.format(worldPoint.getX());
y1 = df.format(worldPoint.getY());

x3 = Math.round(p.getX()*100.0)/100.0;
y3 = Math.round(p.getY()*100.0)/100.0;

System.out.println("x2" + x2);
System.out.println("y2" + y2);
System.out.println("x3" + x3);
System.out.println("y3" + y3);

if(( x2+0.05 >= x3)&&(x2-0.05 <=x3))
{
if(( y2+0.05 >= y3)&&(y2-0.05 <=y3))
{
flag=1;

frame = new JFrame();
frame.setTitle("Specialities of the Restaurant");
ImageIcon image = new ImageIcon("rest.jpg");
JLabel background = new JLabel(image);
background.setBounds(0, 0, image.getIconWidth(),image.getIconHeight());

frame.setSize(400, 250);
frame.getContentPane().add(pane1,BorderLayout.NORTH);
frame.getContentPane().add(pane2,BorderLayout.CENTER);

gridBagConstraints = new java.awt.GridBagConstraints();
pane2.setLayout(new java.awt.GridBagLayout());
gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 0;
gridBagConstraints.weightx = 1.0;
pane1.add(background);
pane1.setBackground(c);
pane2.setBackground(c1);

x1label = new JLabel("X Co-ordinate: " + x1);

gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 4;
gridBagConstraints.weightx = 1.0;

pane2.add(x1label, gridBagConstraints);
x2label = new JLabel("Y Co-ordinate: " + y1);

gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 5;
gridBagConstraints.weightx = 1.0;

pane2.add(x2label, gridBagConstraints);       
addr = new JLabel(address1[i]);
System.out.println(address1[i]);
JOptionPane.showMessageDialog(null,"Latitude :"+y1+"\n"+"Longitude :"+x1+"\n"+"Address: "+address1[i],"INDIAN RESTAURANTS",JOptionPane.INFORMATION_MESSAGE,new ImageIcon(address[i]));
System.out.println(address1[i]);
gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 6;
gridBagConstraints.weightx = 1.0;
pane2.add(addr, gridBagConstraints);
frame.setVisible(true);
System.out.println("Address: " +address1[i]);

}
}
if(flag == 1)
break;
}
// statusLabel.setText(s);
// }
//else
//statusLabel.setText("X:0.000 Y:0.000");
}
});
}

public void setMap(com.esri.mo2.ui.bean.Map map1)
{
map = map1;
}
}
class XYfeatureLayer extends BaseFeatureLayer
{
BaseFields fields;
//Indicate mouse pressed and mouse released
//private boolean mousePressed = false;
//private boolean mouseReleased = false;

private java.util.Vector featureVector;
public XYfeatureLayer(BasePointsArray bpa,Map map,String address[],String address1[])
{
createFeaturesAndFields(bpa,map,address,address1);
BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
setFeatureClass(bfc);
BaseSimpleRenderer srd = new BaseSimpleRenderer();
//SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
//sms.setSymbolColor(new Color(255,0,0));
//sms.setWidth(5);
TrueTypeMarkerSymbol ttm=new TrueTypeMarkerSymbol();
ttm.setFont(new Font("ESRI Crime Analysis",Font.PLAIN,40));
ttm.setColor(new Color(0,0,250));
ttm.setCharacter("114");
srd.setSymbol(ttm);
setRenderer(srd);
// without setting layer capabilities, the points will not
// display (but the toc entry will still appear)
XYLayerCapabilities lc = new XYLayerCapabilities();
setCapabilities(lc);
}
private void createFeaturesAndFields(BasePointsArray bpa,Map map,String address[], String address1[])
{
featureVector = new java.util.Vector();
fields = new BaseFields();
createDbfFields();
for(int i=0;i<bpa.size();i++)
{
BaseFeature feature = new BaseFeature(); //feature is a row
feature.setFields(fields);

com.esri.mo2.cs.geom.Point p = new com.esri.mo2.cs.geom.Point(bpa.getPoint(i));

feature.setValue(0,p);
// feature.setValue(1,new Integer(0)); // point data
feature.setValue(1,address[i]);
feature.setDataID(new BaseDataID("MyPoints",i));
featureVector.addElement(feature);
}
}

private void createDbfFields()
{
fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
fields.addField(new BaseField("Address",java.sql.Types.VARCHAR,16,0));
}

public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa)
{
com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
try
{
featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,fields);
}
catch (IllegalArgumentException iae) {}
featClass.setName(name);

for (int i=0;i<bpa.size();i++)
{
featClass.addFeature((Feature) featureVector.elementAt(i));
System.out.println("featClass" + featureVector.elementAt(i));
}
return featClass;
}

private final class XYLayerCapabilities extends com.esri.mo2.map.dpy.LayerCapabilities
{
XYLayerCapabilities()
{
for (int i=0;i<this.size(); i++)
{
setAvailable(this.getCapabilityName(i),true);
setEnablingAllowed(this.getCapabilityName(i),true);
getCapability(i).setEnabled(true);
}
}
}
}

class AttrTab extends JDialog
{
JPanel panel1 = new JPanel();
com.esri.mo2.map.dpy.Layer layer = Project.layer4;
JTable jtable = new JTable(new MyTableModel());
JScrollPane scroll = new JScrollPane(jtable);

public AttrTab() throws IOException
{
setBounds(70,70,450,350);
setTitle("Attribute Table");
addWindowListener(new WindowAdapter()
{
public void windowClosing(WindowEvent e)
{
setVisible(false);
}
});
scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
// next line necessary for horiz scrollbar to work
jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

TableColumn tc = null;
int numCols = jtable.getColumnCount();
//jtable.setPreferredScrollableViewportSize(
//new java.awt.Dimension(440,340));
for (int j=0;j<numCols;j++)
{
tc = jtable.getColumnModel().getColumn(j);
tc.setMinWidth(50);
}
getContentPane().add(scroll,BorderLayout.CENTER);
}
}

class MyTableModel extends AbstractTableModel
{
// the required methods to implement are getRowCount,
// getColumnCount, getValueAt
com.esri.mo2.map.dpy.Layer layer = Project.layer4;
MyTableModel()
{
qfilter.setSubFields(fields);
com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
while (cursor.hasMore())
{
ArrayList inner = new ArrayList();
Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
inner.add(0,String.valueOf(row));
for (int j=1;j<fields.getNumFields();j++)
{
inner.add(f.getValue(j).toString());
}
data.add(inner);
row++;
}
}
FeatureLayer flayer = (FeatureLayer) layer;
FeatureClass fclass = flayer.getFeatureClass();
String columnNames [] = fclass.getFields().getNames();
ArrayList data = new ArrayList();
int row = 0;
int col = 0;
BaseQueryFilter qfilter = new BaseQueryFilter();
Fields fields = fclass.getFields();

public int getColumnCount()
{
return fclass.getFields().getNumFields();
}

public int getRowCount()
{
return data.size();
}

public String getColumnName(int colIndx)
{
return columnNames[colIndx];
}

public Object getValueAt(int row, int col)
{
ArrayList temp = new ArrayList();
temp =(ArrayList) data.get(row);
return temp.get(col);
}
}

class CreateShapeDialog extends JDialog
{
String name = "";
String path = "";

JButton ok = new JButton("OK");
JButton cancel = new JButton("Cancel");

JTextField nameField = new JTextField("enter layer name here, then hit ENTER",25);
com.esri.mo2.map.dpy.FeatureLayer selectedlayer;

ActionListener lis = new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
Object o = ae.getSource();
if (o == nameField)
{
name = nameField.getText().trim();
path = ((ShapefileFolder)(Project.layer4.getLayerSource())).getPath();
System.out.println(path+" " + name);
}
else if (o == cancel)
setVisible(false);
else
{
try
{
ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,2);
}
catch(Exception e)
{
System.out.println("write error");
}
setVisible(false);
}
}
};

JPanel panel1 = new JPanel();
JLabel centerlabel = new JLabel();
//centerlabel;
CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5)
{
selectedlayer = layer5;
setBounds(40,350,450,150);
setTitle("Create new shapefile?");
addWindowListener(new WindowAdapter()
{
public void windowClosing(WindowEvent e)
{
setVisible(false);
}
});
nameField.addActionListener(lis);
ok.addActionListener(lis);
cancel.addActionListener(lis);
String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
"the new name you want for the layer and click OK.<BR>" +
"You can then add it to the map in the usual way.<BR>"+
"Click ENTER after replacing the text with your layer name";
centerlabel.setHorizontalAlignment(JLabel.CENTER);
centerlabel.setText(s);
getContentPane().add(centerlabel,BorderLayout.CENTER);

panel1.add(nameField);
panel1.add(ok);
panel1.add(cancel);

getContentPane().add(panel1,BorderLayout.SOUTH);
}
}

class Arrow extends Tool
{
public void mouseClicked(MouseEvent me)
{
}

public void arrowChores()
{
Project.milesLabel.setText("DIST 0 mi ");
Project.kmLabel.setText(" 0 km ");
Project.map.remove(Project.acetLayer);
Project.acetLayer = null;
Project.map.repaint();
}
}

class Flash extends Thread
{
Legend legend;
Flash(Legend legendin)
{
legend = legendin;
}

public void run()
{
for (int i=0;i<12;i++)
{
try
{
Thread.sleep(500);
legend.toggleSelected();
} catch (Exception e) {}
}
}
}

class DistanceTool extends DragTool
{
int startx,starty,endx,endy,currx,curry;
com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
double distance;

public void mousePressed(MouseEvent me)
{
startx = me.getX(); starty = me.getY();
initPoint = Project.map.transformPixelToWorld(me.getX(),me.getY());
}

public void mouseReleased(MouseEvent me)
{
// now we create an acetatelayer instance and draw a line on it
endx = me.getX(); endy = me.getY();
endPoint = Project.map.transformPixelToWorld(me.getX(),me.getY());
distance = (69.44 / (2*Math.PI)) * 360
* Math.acos(Math.sin(initPoint.y * 2 * Math.PI / 360)
* Math.sin(endPoint.y * 2 * Math.PI / 360)+ Math.cos(initPoint.y * 2
* Math.PI / 360)* Math.cos(endPoint.y * 2 * Math.PI / 360)
* (Math.abs(initPoint.x - endPoint.x) < 180 ?Math.cos((initPoint.x - endPoint.x)*2
* Math.PI/360):Math.cos((360 - Math.abs(initPoint.x -endPoint.x))*2
* Math.PI/360)));
System.out.println( distance );

Project.milesLabel.setText("DIST: " + new Float((float)distance).toString() + " mi ");
Project.kmLabel.setText(new Float((float)(distance*1.6093)).toString() + "km");

if (Project.acetLayer != null)

Project.map.remove(Project.acetLayer);

Project.acetLayer = new AcetateLayer()
{
public void paintComponent(java.awt.Graphics g)
{
java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
g2d.setColor(new Color(0,0,250));
g2d.draw(line);
}
};
Graphics g = super.getGraphics();
Project.map.add(Project.acetLayer);
Project.map.redraw();
}
public void cancel() {};
}


class HotImage extends JDialog
{
Image image;
public HotImage(Image image1)
{
setBounds(50,50,400,400);
image = image1;
Container cp =getContentPane(); 
MyJpanel jp = new MyJpanel();
cp.add(jp);
}

class MyJpanel extends JPanel 
{
public void paintComponent(Graphics g)
{
super.paintComponent(g);
g.drawImage(image,90,90,this);
}
} 
}


class Hotlink extends Tool 
{
Hotlink hotlink = new Hotlink();

Toolkit tk = Toolkit.getDefaultToolkit();
public Image bolt = tk.getImage("hotlink.jpg");
java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new java.awt.Point(3,15),"bolt"); 

public void mouseClicked(MouseEvent me)
{
System.out.println("allo allo");
Map map = Project.map;
com.esri.mo2.cs.geom.Point point =map.transformPixelToWorld (me.getX(),me.getY());
if(true)
{
Image image = Toolkit.getDefaultToolkit().getImage("hotlink.jpg");
HotImage hotimage = new HotImage(image);
hotimage.setVisible(true);
hotlink.setCursor(boltCursor);

}
}
}