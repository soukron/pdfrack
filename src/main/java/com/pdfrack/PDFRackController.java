package com.pdfrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        new ArrayList();
        PDFFileUtil util = new PDFFileUtil();
        util.setDirectory(directory);
        ArrayList<PDFFile> mags = util.getAllMagazines();

        for(int i = 0; i < mags.size() && i <= number; ++i) {
            PDFFile currentMag = (PDFFile)mags.get(i);
            theHTML.append("<a href='/data/" + currentMag.getTheFile().getParentFile().getName() + "/" + currentMag.getTheFile().getName() + "'>");
            theHTML.append("<img src='/data/" + currentMag.getTheFile().getParentFile().getName() + "/preview.jpg' height='200' width='152'></a>\n");
        }

        return theHTML.toString();
    }
}

