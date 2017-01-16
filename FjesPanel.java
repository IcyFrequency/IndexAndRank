
package net.semanticmetadata.lire.sampleapp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

//@SuppressWarnings("serial")
public class FjesPanel extends JPanel{


	private BufferedImage img;
    int teller = 0;

	/*public FjesPanel(){
		super();
	}*/

	public void tegnFjes(BufferedImage img){
		this.img = img;
	}
	public void paintComponent(Graphics grp){
		super.paintComponent(grp);

		if(this.img == null){
			System.out.println("J panel img is null :  !!");
			return;

		}
		grp.drawImage(img,10,10,img.getWidth(),img.getWidth(),null);
		grp.setFont(new Font("arial",2,20));
		grp.setColor(Color.white);
		grp.drawString("prosesser1 kamera frame [frame: "+ (teller++) + " ] ", 50, 50);
	}
}
