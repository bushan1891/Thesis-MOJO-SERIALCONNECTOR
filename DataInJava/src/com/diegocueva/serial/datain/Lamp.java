package com.diegocueva.serial.datain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Lamp extends DrawnPanel{
	
	/** Serial version UID 	 */
	private static final long serialVersionUID = -7494831192161401876L;
	

	/**
	 * Must be overriden
	 */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int red = (value*255)/1023;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(new Color(red, 0, 0));
        g2d.fillOval(10, 40, 180, 180);
        
        g2d.setColor(Color.yellow);
        g2d.drawOval(10, 40, 180, 180);        
    }	
	

}
