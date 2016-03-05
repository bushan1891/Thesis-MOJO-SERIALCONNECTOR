package com.diegocueva.serial.datain;

import java.awt.Font;

import javax.swing.JLabel;

public class MLabel extends JLabel{
	
	/** Serial version UID */
	private static final long serialVersionUID = 6206875506469171849L;
	
	private static final Font font = new Font("COURIER NEW", Font.BOLD, 23);
	
	public MLabel(String text){
		super(text, JLabel.HORIZONTAL);
		this.setFont(font);
	}

}
