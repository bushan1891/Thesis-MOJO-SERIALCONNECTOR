package com.diegocueva.serial.datain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class BarPanel extends DrawnPanel{
	
	/** Serial version UID 	 */
	private static final long serialVersionUID = -7494831192161401876L;
	

	/**
	 * Must be overriden
	 */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height = (value*280)/1023;
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setColor(new Color(0, 0, 128));
        g2d.fillRect(40, 280-height+20, 120, height);

        g2d.setColor( Color.black );
        g2d.drawRect(40, 280-height+20, 120, height);

        
    }	
	

}
