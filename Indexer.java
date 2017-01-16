
package net.semanticmetadata.lire.sampleapp;

import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.utils.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import static org.apache.commons.io.FileUtils.deleteDirectory;


public class Indexer {

    private static Boolean s_isConsole = null;
    private static final char ESC = 27;

    private static boolean isConsole() {
        if (s_isConsole == null)
            s_isConsole = (null != System.console());
        return s_isConsole;
    }

    private static void clearScreen() {
        if (!isConsole())
            return;
        System.out.print(ESC + "[2J");
        System.out.flush();
    }

    public static void main(String[] args) throws IOException {
        // Checking if arg[0] is there and if it is a directory.
        //See if any directory was given
        File directory = new File(args[0]);
        boolean passed = false;
        if (args.length > 0) {
            if (directory.exists() && directory.isDirectory()) passed = true;

            String pathIndex = directory + "Index";
            File toBeDel = new File(pathIndex);

            if (!toBeDel.exists()){
                System.out.println("Directory does not exist.");
            }
            else{
                try{
                    delete(toBeDel);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        //If no directory was parsed
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
            System.exit(1);
        }

        clearScreen();
        System.out.println("Indexing images in " + args[0]);
        // Getting all images from a directory and its sub directories.
        ArrayList<String> images = FileUtils.getAllImages(new File(args[0]), true);

        // Creating a JCD document builder and indexing all files.
        GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(JCD.class);
        // and here we add those features we want to extract in a single run:
        globalDocumentBuilder.addExtractor(FCTH.class);
        globalDocumentBuilder.addExtractor(AutoColorCorrelogram.class);
        globalDocumentBuilder.addExtractor(CEDD.class);
        globalDocumentBuilder.addExtractor(JCD.class);

        String directoryIndex = args[0]+ "Index";

        // Creating an Lucene IndexWriter
        IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
        IndexWriter iw = new IndexWriter(FSDirectory.open(Paths.get(directoryIndex)), conf);
        // Iterating through images building the low level features
        for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
            String imageFilePath = it.next();
            System.out.println("Indexing " + imageFilePath);
            try {
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                Document document = globalDocumentBuilder.createDocument(img, imageFilePath);
                iw.addDocument(document);
            } catch (Exception e) {
                System.err.println("Error reading image or indexing it.");
                e.printStackTrace();
            }
        }
        // closing the IndexWriter
        iw.close();
        System.out.println("Finished indexing.");
    }

    private static void delete(File file) throws IOException{
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());
            }
            else{
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }
        }
        else{
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
}