package com.pdfrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

@RestController
public class PDFRackController {
    @Autowired
    Environment environment;

    @RequestMapping("hello")
    public String sayHello(){
        return ("Hello, " + environment.getProperty("media"));
    }

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
        ArrayList<PDFFile> titles = util.getAllMagazines();

        // if we don't have any books, download a couple of OpenShift books
        if(titles.size() < 1) {
            this.downloadBooks(directory);
            titles = util.getAllMagazines();
        }

        for(int i = 0; i < titles.size() && i <= number; ++i) {
            PDFFile currentTitle = (PDFFile)titles.get(i);
            theHTML.append("<a href='/data/" + currentTitle.getTheFile().getParentFile().getName() + "/" + currentTitle.getTheFile().getName() + "'>");
            theHTML.append("<img src='/data/" + currentTitle.getTheFile().getParentFile().getName() + "/" + currentTitle.getTheFile().getName() +"_preview.jpg' height='200' width='152'></a>\n");
        }

        return theHTML.toString();
    }

    private void downloadBooks(String directory) {
        String developersBook = new String("https://assets.openshift.com/hubfs/pdfs/OpenShift_for_Developers_Red_Hat.pdf");
        String deployBook = new String("https://assets.openshift.com/hubfs/pdfs/Deploying_to_OpenShift.pdf");

        try {
            FileUtils.copyURLToFile(new URL(developersBook), new File(directory + "/developers.pdf"));
            FileUtils.copyURLToFile(new URL(deployBook), new File(directory + "/deploy.pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

