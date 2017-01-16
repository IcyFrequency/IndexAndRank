package net.semanticmetadata.lire.sampleapp;

import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Searcher{
    //getKey() and getValue(), use iterator
    private HashMap<String, Double> hmap = new HashMap<String, Double>();

    /*public Searcher() {
        super();
    }*/

    void Rank(BufferedImage args) throws IOException {
            //System.out.println("\n");

            //Your path to your indexed images
            //String pathPos = "Users/pedramsherafat/Dropbox/Utdanning/Master/Images/both/Index";
            String pathPos = "/Users/pedramsherafat/Pictures/ProjectData/Index";
            //String pathNeg = "C:\\Users\\SherWorkHorse\\Documents\\ProjectData\\Images\\neg\\Index";
            //String path = args[0];
            //path = path.substring(0, path.length()- 23);
            IndexReader irPos = DirectoryReader.open(FSDirectory.open(Paths.get(pathPos)));
            //IndexReader irNeg = DirectoryReader.open(FSDirectory.open(Paths.get(pathNeg)));

            ImageSearcher searcherPos = new GenericFastImageSearcher(100, JCD.class);

            /*ImageSearcher searcherPos2 = new GenericFastImageSearcher(30, AutoColorCorrelogram.class);
            ImageSearcher searcherNeg = new GenericFastImageSearcher(30, CEDD.class);
            ImageSearcher searcherPos2 = new GenericFastImageSearcher(30, AutoColorCorrelogram.class);*/

            // searching with a image file ...
            ImageSearchHits hits = searcherPos.search(args, irPos);

            /*ImageSearchHits hits2 = searcherPos2.search(args, irPos);
             searching with a Lucene document instance ...
            ImageSearchHits hits = searcher.search(ir.document(0), ir);*/


            for (int i = 0; i < hits.length(); i++) {
                String fileName = irPos.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];

                //Print out score and fileName
                System.out.printf("%.2f", hits.score(i));
                System.out.print("\t" + fileName + "\n");
                //System.out.println(hits.score(i) + ": \t" + fileName);

                //hits.length()
                //Here we can decide if we want to use all the elements that are ranked or just the ones with best match to training data.
                if (i < 11) {
                    hmap.put(fileName, hits.score(i));
                }
            }
            decide();
            decideNrTwo();
            //System.out.println("\n");

        /*for (int i = 0; i < hits2.length(); i++) {
            String fileName = ir.document(hits2.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits2.score(i) + ": \t" + fileName);
        }*/
    }

    private boolean decide() {
        String p = "p";
        Double posImg = 0.0;
        Double negImg = 0.0;

        //these int are for avg
        int i = 0;
        int j = 0;

        for (Object o : hmap.entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            String polarity = String.valueOf(pair.getKey());
            polarity = polarity.substring(polarity.lastIndexOf("/") + 1, polarity.lastIndexOf(" "));

            if (polarity.contains(p)) {
                i++;
                double tempP = ((Double) pair.getValue());
                posImg += tempP;
            } else {
                j++;
                double tempN = ((Double) pair.getValue());
                negImg += tempN;
            }
        }
        Double posAvg = Math.abs(posImg / i);
        Double negAvg = Math.abs(negImg / j);
        System.out.println("\n" + posAvg + " \t" + negAvg);


        System.out.println("Decision method 1");
        if (posAvg < negAvg) {
            System.out.println("OBJECT IS IN FRAME--------------");
            return true;
        } else {
            System.out.println("-----------OBJECT NOT IN FRAME");
            return false;
        }
    }

    private boolean decideNrTwo(){
        String p = "p";
        Double posImg = 0.0;
        Double negImg = 0.0;

        double i = 0.1;
        int tellerP = 0;
        int tellerN = 0;

        for (Object o : hmap.entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            String polarity = String.valueOf(pair.getKey());
            //This can be wrong on Mac
            polarity = polarity.substring(polarity.lastIndexOf("/") + 1, polarity.lastIndexOf(" "));

            //System.out.println("\n"+polarity + "");

            if (polarity.contains(p)) {
                tellerP++;
                double tempP = ((Double) pair.getValue()) * i;
                posImg += tempP;
                i = i + 0.1;

            } else {
                tellerN++;
                double tempN = ((Double) pair.getValue()) * i;
                negImg += tempN;
                i = i + 0.1;

            }
        }

        Double posAvg = Math.abs(posImg / tellerP);
        Double negAvg = Math.abs(negImg / tellerN);
        System.out.println("\n" + posAvg + " \t" + negAvg);

        System.out.println("Decision method 2");
        if (posAvg < negAvg) {
            System.out.println("OBJECT IS IN FRAME--------------");
            return true;
        } else {
            System.out.println("-----------OBJECT NOT IN FRAME");
            return false;
        }



    }


    public HashMap getMap() {
        return hmap;
    }

}