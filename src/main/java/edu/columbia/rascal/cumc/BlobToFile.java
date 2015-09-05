package edu.columbia.rascal.cumc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BlobToFile {

    private static final Logger log = LoggerFactory.getLogger(BlobToFile.class);

    public BlobToFile(String dir, String fileName, InputStream in) {
        folder(dir);
        blobToFile(fileName, in);
    }

    private  void folder(String dir) {
        File file = new File(dir);
        if( !file.exists() ) {
            file.mkdirs();
        }
    }

    private void blobToFile(String fileName, InputStream in) {
        try {
            OutputStream os = new FileOutputStream(new File(fileName));
            FileCopyUtils.copy(in, os);
            os.close();
            in.close();
        } catch (Exception e) {
            log.error("caught: ", e);
        }
    }

}
