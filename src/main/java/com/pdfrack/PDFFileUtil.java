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
    public ArrayList<File> pdfFiles = new ArrayList();
    private File magazineDirectory = new File("/home/zero/Stuff/download/magazines");

    public PDFFileUtil() {
    }

    public void setDirectory(String directory) {
        this.magazineDirectory = new File(directory);
    }

    private void getAllMagazineDirectories(File currentFile) {
        File[] listOfFiles = currentFile.listFiles();

        for(int i = 0; i < listOfFiles.length; ++i) {
            if(listOfFiles[i].isDirectory()) {
                this.getAllMagazineDirectories(listOfFiles[i]);
            } else if(listOfFiles[i].getName().toLowerCase().endsWith(".pdf")) {
                this.pdfFiles.add(listOfFiles[i]);
            }
        }

    }

    public ArrayList<PDFFile> getAllMagazines() {
        ArrayList<PDFFile> mags = new ArrayList();
        this.getAllMagazineDirectories(this.magazineDirectory);
        this.pdfFiles.sort(new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(Long.valueOf(f2.lastModified()));
            }
        });
        Collections.reverse(this.pdfFiles);
        Iterator var2 = this.pdfFiles.iterator();

        while(var2.hasNext()) {
            File tempFile = (File)var2.next();
            PDFFile theMag = new PDFFile();
            theMag.setTheFile(tempFile);
            mags.add(theMag);
            this.createPreviewImageFromPDF(theMag);
        }

        return mags;
    }

    private void createPreviewImageFromPDF(PDFFile theMag) {
        try {
            File previewImage = new File(theMag.getTheFile().getParentFile() + "/preview.jpg");
            if(!previewImage.exists()) {
                PDDocument document = PDDocument.load(theMag.getTheFile());
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                System.out.println("getting " + theMag.theFile.getName());
                BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 22.0F, ImageType.RGB);
                ImageIOUtil.writeImage(bim, theMag.getTheFile().getParentFile().getAbsolutePath() + "/preview.jpg", 300);
                document.close();
            }
        } catch (Exception var6) {
            System.out.println(theMag.getTheFile().getName());
            var6.printStackTrace();
        }

    }
}
