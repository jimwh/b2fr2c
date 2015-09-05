package edu.columbia.rascal.cumc;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RascalZipper {

    public static final String ZipFileRootDirectory = "/rascal_to_cumc";

    private static final Logger log = LoggerFactory.getLogger(RascalZipper.class);

    private File dir;
    private String zipFileName;

    public RascalZipper() {
        DateTime dateTime = DateTime.now();
        this.zipFileName = Extractor.RootDirectory + "_" + dateTime.toString("yyyyMMdd") + ".zip";
        this.dir = new File(Extractor.RootDirectory);
    }

    public RascalZipper(String dir, String zipFileName) {
        this.dir = new File(dir);
        this.zipFileName = zipFileName;
    }

    public void zipFiles() throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        addDir(dir, out);
        out.close();
    }

    private void addDir(File dir, ZipOutputStream out) throws IOException {
        File[] listFiles = dir.listFiles();
        byte[] buf = new byte[4096];

        for(File file : listFiles) {
            if (file.isDirectory()) {
                addDir(file, out);
                continue;
            }
            FileInputStream in = new FileInputStream(file.getCanonicalFile());
            String path = file.getCanonicalPath();
            path = path.substring(path.indexOf(ZipFileRootDirectory));
            out.putNextEntry(new ZipEntry(path));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }

}
