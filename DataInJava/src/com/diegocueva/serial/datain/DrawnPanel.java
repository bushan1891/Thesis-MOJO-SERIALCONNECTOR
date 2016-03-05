package com.diegocueva.serial.datain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


/**
 * 
 * Area to drawn 
 * 
 * @author Diego Cueva
 *
 */
public class DrawnPanel extends JPanel {

	/** Serial version UID */
	private static final long serialVersionUID = 8831595896454044895L;

	/** Value to show */
	protected int value=0;

	/**
	 * Set value to show
	 * @param value
	 */
	protected void setValue(int value){
		this.value = value;
	}
	
	public DrawnPanel(){
		this.setBackground( Color.WHITE );
		this.setPreferredSize( new Dimension( 200, 300 ) );	
	}
	
	/**
	 * Must be overriden
	 */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, 200, 300);
    }	
	
}
