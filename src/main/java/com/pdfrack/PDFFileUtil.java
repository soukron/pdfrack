package com.pdfrack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class PDFFileUtil {
    public ArrayList<File> pdfFiles = new ArrayList<File>();
    private File fileDirectory = new File(".");

    public PDFFileUtil() {
    }

    public void setDirectory(String directory) {
        this.fileDirectory = new File(directory);
    }

    private void getAllFileDirectories(File currentFile) {
        File[] listOfFiles = currentFile.listFiles();

        for(int i = 0; i < listOfFiles.length; ++i) {
            if(listOfFiles[i].isDirectory()) {
                this.getAllFileDirectories(listOfFiles[i]);
            } else if(listOfFiles[i].getName().toLowerCase().endsWith(".pdf")) {
                this.pdfFiles.add(listOfFiles[i]);
            }
        }

    }

    public ArrayList<PDFFile> getAllPDFs() {
        ArrayList<PDFFile> titles = new ArrayList<PDFFile>();
        this.getAllFileDirectories(this.fileDirectory);
        this.pdfFiles.sort(new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(Long.valueOf(f2.lastModified()));
            }
        });
        Collections.reverse(this.pdfFiles);
        Iterator var2 = this.pdfFiles.iterator();

        while(var2.hasNext()) {
            File tempFile = (File)var2.next();
            PDFFile theTitle = new PDFFile();
            theTitle.setTheFile(tempFile);
            titles.add(theTitle);
            this.createPreviewImageAndMetaDataFromPDF(theTitle);
        }

        return titles;
    }

    private void createPreviewImageAndMetaDataFromPDF(PDFFile theTitle) {
        try {
            File previewImage = new File(theTitle.getTheFile().getParentFile() + "/" + theTitle.theFile.getName() + "_preview.jpg");

            // Create the page count metadata
            PDDocument document = PDDocument.load(theTitle.getTheFile());
            theTitle.setPages(document.getNumberOfPages());

            if(!previewImage.exists()) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                System.out.println("getting " + theTitle.theFile.getName());
                BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 50.0F, ImageType.RGB);
                ImageIOUtil.writeImage(bim, theTitle.getTheFile().getParentFile().getAbsolutePath() + "/" + theTitle.theFile.getName() + "_preview.jpg", 300);
                document.close();
            }
        } catch (Exception var6) {
            System.out.println(theTitle.getTheFile().getName());
            var6.printStackTrace();
        }

    }
}
