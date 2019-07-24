package com.pdfrack;

import java.io.File;

public class PDFFile {
    File theFile;
    int pages;

    public PDFFile() {
        pages = 0;
    }

    public File getTheFile() {
        return this.theFile;
    }

    public void setTheFile(File theFile) {
        this.theFile = theFile;
    }
    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
