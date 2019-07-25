package com.pdfrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

@RestController
public class PDFRackController {
    @Autowired
    Environment environment;

    @RequestMapping("rack/{num}")
    public String getRack(@PathVariable int num) {

        return this.getHTML(num, environment.getProperty("media"));
    }

    @RequestMapping("/")
    public String defaultRoute() {
        return this.getRack(100);
    }

    @RequestMapping("rack/")
    public String getEmptyRack() {
        return this.getRack(25);
    }


    private String getHTML(int number, String directory) {
        StringBuffer theHTML = new StringBuffer();
        PDFFileUtil util = new PDFFileUtil();
        util.setDirectory(directory);
        ArrayList<PDFFile> titles = util.getAllPDFs();

        // Setup the HTML page
        theHTML.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "       <style>\n" +
                "  .bordered {\n" +
                "    padding: 5px;\n" +
                "    border: 1px solid darkorange;\n" +
                "    border-radius: 8px;\n" +
                "  }\n" +
                "</style>" +
                "       <link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' integrity='sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T' crossorigin='anonymous'>" +
                "        <title>PDF Rack</title>\n" +
                "    </head>\n" +
                "    <body>" +
                "    <div class='container'>" +
                "       <div class='row'>");


        // if we don't have any books, download a couple of OpenShift books
        if (titles.size() < 1) {
            this.downloadBooks(directory);
            titles = util.getAllPDFs();
        }

        for (int i = 0; i < titles.size() && i <= number; ++i) {
            PDFFile currentTitle = (PDFFile) titles.get(i);
            System.out.println("Title is " + currentTitle.getTheFile().getName());
            theHTML.append("<div class=\"col-6 col-md-4 col-lg-3 bordered\"><a href='/data/" + currentTitle.getTheFile().getParentFile().getName() + "/" + currentTitle.getTheFile().getName() + "'>");
            theHTML.append("<img width='90%' src='/data/" + currentTitle.getTheFile().getParentFile().getName() + "/" + currentTitle.getTheFile().getName() + "_preview.jpg' ></a><br>" +
                    "Filename: " + currentTitle.getTheFile().getName() +
                    "<br>" +
                    "Pages: " + currentTitle.getPages() +
                    "</div>\n");
        }

        theHTML.append("</div></div></body></html>");
        return theHTML.toString();
    }

    private void downloadBooks(String directory) {
        String developersBook = new String("https://assets.openshift.com/hubfs/pdfs/OpenShift_for_Developers_Red_Hat.pdf");
        String deployBook = new String("https://assets.openshift.com/hubfs/pdfs/Deploying_to_OpenShift.pdf");

        System.setProperty("http.agent", "Chrome");

        // Using nio so it will not consume memory in the container
        try {
            this.downloadWithJavaNIO(deployBook, directory + "/deploy.pdf");
            this.downloadWithJavaNIO(developersBook, directory + "/developer.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadWithJavaNIO(String fileURL, String localFilename) throws IOException {
        URL url = new URL(fileURL);
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(localFilename); FileChannel fileChannel = fileOutputStream.getChannel()) {

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
