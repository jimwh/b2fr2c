package edu.columbia.rascal.cumc;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class RascalZipper {

    private static final Logger log = LoggerFactory.getLogger(RascalZipper.class);

    private File dir;
    private String zipFileName;
    private String zipFileRootDirectory;

    public void zipFiles(String downloadFileDirectory) throws IOException {

        setUpZipFileDirectory(downloadFileDirectory);

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        addDir(dir, out);
        out.close();
    }

    private void setUpZipFileDirectory(String downloadFileDirectory) {
        this.zipFileRootDirectory =
                downloadFileDirectory.substring(downloadFileDirectory.lastIndexOf(File.separator));
        DateTime dateTime = DateTime.now();
        this.zipFileName = downloadFileDirectory + "_" + dateTime.toString("yyyyMMdd") + ".zip";
        this.dir = new File(downloadFileDirectory);
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
            path = path.substring(path.indexOf(zipFileRootDirectory));
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
