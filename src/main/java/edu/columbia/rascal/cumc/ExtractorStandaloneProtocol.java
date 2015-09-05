package edu.columbia.rascal.cumc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtractorStandaloneProtocol implements RowCallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtractorStandaloneProtocol.class);

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {

        Blob blob = resultSet.getBlob("DOCUMENTDATASTAMPED");
        if( blob == null ) {
            blob = resultSet.getBlob("DOCUMENTDATA");
            if( blob == null )
                throw new SQLException("foo barr...");
        }

        String protocolNumber = resultSet.getString("PROTOCOLNUMBER");
        int protocolYear = resultSet.getInt("PROTOCOLYEAR");
        int modificationNumber = resultSet.getInt("MODIFICATIONNUMBER");
        String fileName = resultSet.getString("FILENAME");
        InputStream in = blob.getBinaryStream();
        String dirName = String.format("%s_Y%02d_M%02d", protocolNumber, protocolYear, modificationNumber);
        String folder = Extractor.RootDirectory + File.separator+dirName + File.separator + "ATTACHED_STANDALONE_PROTOCOLS";
        folder(folder);
        fileName = folder + File.separator + fileName;
        blobToFile(fileName, in);
    }

    private void folder(String dir) {
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
