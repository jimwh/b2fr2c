package edu.columbia.rascal.cumc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtractorStandaloneProtocol implements RowCallbackHandler {

    public static final String
            SQL_STANDALONE_PROTOCOL = "select H.OID, P.PROTOCOLNUMBER, H.protocolYear, H.modificationnumber,"
            + "A.FILENAME, A.DOCUMENTDATA, A.DOCUMENTDATASTAMPED, A.DOCUMENTIDENTIFIER"
            + " from IRBPROTOCOLHEADER H "
            + " join IRBPROTOCOL P on H.PARENTPROTOCOLOID=P.OID "
            + " join IRBSTATUS S on S.PARENTOBJECTOID=H.OID "
            + " join IRBATTACHMENT A on A.IRBPROTOCOLHEADERID=H.OID "
            + " where"
            + " H.OID = (select max(iph.OID) from IrbProtocolHeader iph"
            + " where iph.PARENTPROTOCOLOID = H.PARENTPROTOCOLOID) and"
            + " S.OID = (select max(ss.OID) from IrbStatus ss where ss.PARENTOBJECTOID=H.OID) and"
            + " H.IRBAPPROVALDATE is not null and"
            + " trunc(H.EXPIRATIONDATE) >= trunc(sysdate) and"
            + " S.STATUSNAME='Approved' and"
            + " A.ATTACHMENTTYPECODE=8 and"
            + " A.ARCHIVE='N' and A.ACTIVE='Y' "
            + " and P.PROTOCOLNUMBER='AAAJ7852'";

    private static final Logger log = LoggerFactory.getLogger(ExtractorStandaloneProtocol.class);

    private final String downloadDirectory;
    public ExtractorStandaloneProtocol(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {

        Blob blob = resultSet.getBlob("DOCUMENTDATASTAMPED");
        if( blob == null ) {
            blob = resultSet.getBlob("DOCUMENTDATA");
            if( blob == null )
                throw new SQLException("foo barr...");
        }
        //
        String protocolNumber = resultSet.getString("PROTOCOLNUMBER");
        int protocolYear = resultSet.getInt("PROTOCOLYEAR");
        int modificationNumber = resultSet.getInt("MODIFICATIONNUMBER");
        String fileName = resultSet.getString("FILENAME");
        InputStream in = blob.getBinaryStream();
        //
        String dirName = String.format("%s_Y%02d_M%02d", protocolNumber, protocolYear, modificationNumber);
        String folder = downloadDirectory + File.separator+dirName + File.separator + "ATTACHED_STANDALONE_PROTOCOLS";
        fileName = folder + File.separator + fileName;
        new BlobToFile(folder, fileName, in);
    }

}
