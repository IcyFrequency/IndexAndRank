
package net.semanticmetadata.lire.sampleapp;

import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {
    public static void main(String[] args) throws IOException {
        // Checking if arg[0] is there and if it is an image.
        BufferedImage img = null;
        boolean passed = false;
        File f = new File(args[0]);
        if (args.length > 0) {
            //File f = new File(args[0]);
            if (f.exists()) {
                try {
                    img = ImageIO.read(f);
                    passed = true;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        if (!passed) {
            System.out.println("No image given as first argument.");
            System.out.println("Run \"Searcher <query image>\" to search for <query image>.");
            System.exit(1);
        }
        System.out.println(args[0]+"\n");
        String path = args[0];
        path = path.substring(0, path.length() - 5);
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get( path +"\\Index")));
        ImageSearcher searcher = new GenericFastImageSearcher(30, CEDD.class);
        ImageSearcher searcher2 = new GenericFastImageSearcher(30, AutoColorCorrelogram.class);

        // searching with a image file ...
        ImageSearchHits hits = searcher.search(img, ir);
        ImageSearchHits hits2 = searcher2.search(img, ir);
        // searching with a Lucene document instance ...
//        ImageSearchHits hits = searcher.search(ir.document(0), ir);
        for (int i = 0; i < hits.length(); i++) {
            String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits.score(i) + ": \t" + fileName);

        }

        System.out.println("\n");

        for (int i = 0; i < hits2.length(); i++) {
            String fileName = ir.document(hits2.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits2.score(i) + ": \t" + fileName);
        }
    }
}
