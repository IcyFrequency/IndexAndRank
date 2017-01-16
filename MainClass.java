package net.semanticmetadata.lire.sampleapp;
//-Djava.library.path=/usr/local/Cellar/opencv/2.4.13.1/share/OpenCV/java
//-Djava.library.path=<C:\opencv\build\java>
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.awt.image.BufferedImage;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import java.awt.*;
import javax.swing.*;

//@SuppressWarnings("serial")
public class MainClass extends JFrame {

	public static void main (String [] args) throws InterruptedException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Wellcome to OpenCv version : " + Core.VERSION);

		Calendar cal = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println( sdf.format(cal.getTime()) );

		JFrame frame1 = new JFrame(" Ramme ");
		JButton pause = new JButton("Pause");

		MatToBufferedImg matToBufferedImg = new MatToBufferedImg();
		FjesPanel window = new FjesPanel();
        System.out.println("Fjes");
		Searcher searchInstance = new Searcher();

		frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame1.setSize(500, 500);
		//pause.setBounds(10, 10, 10, 10);

		frame1.setContentPane(window);
		//frame1.setLayout(null);
		frame1.add(pause);
		VideoCapture cam = new VideoCapture(0); //Enable camera
		System.out.println("grab = " +cam.grab()); 
		if(!cam.isOpened()){
			System.out.println("Camera not found");
		}
		else if(cam.isOpened())
		{
			System.out.println("Camera enabled \n");
			Mat Kamera = new Mat();
			frame1.setVisible(true);

			int i = 0;
			//Thread.sleep(500);

            //Thread t = new Thread(searchInstance);

			while (true){
                FPScounter.StartCounter();


				i++;
				System.out.println(i);
				cam.read(Kamera); //Grabs, decodes and returns the next video frame.

				if(!Kamera.empty()){
					//System.out.println("");
					//Thread.sleep(500);
                    matToBufferedImg.settMatrix(Kamera, ".jpg");
                    BufferedImage bufImg = matToBufferedImg.hentBufferedImg();

                    //-----------FIRST
                    try {
                        searchInstance.Rank(bufImg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //-----------SECOND
                    /*Thread t = new Thread(new Runnable(){
                        public void run() {
                            try {
                                searchInstance.Rank(bufImg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });*/

                    /*//-----------THIRD
                   new Thread(() -> {
                        try {
                            searchInstance.Rank(bufImg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });*/


                    //t.start();
					window.tegnFjes(bufImg);
					window.repaint();
					//System.out.println("");
                    FPScounter.StopAndPost();
				}
				else {
					System.out.println("Error, Kamera fanger ikke bilder");
					break;
				}
			}
			cam.release(); //Close camera
            System.exit(1);
		}
	}
}
