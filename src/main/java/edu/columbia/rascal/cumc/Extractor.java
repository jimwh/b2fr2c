package edu.columbia.rascal.cumc;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.List;

@Component
public class Extractor {

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

    private static final Logger log = LoggerFactory.getLogger(Extractor.class);
    private final JdbcTemplate jdbcTemplate;
    public static final String RootDir = "/tmp/rascal_to_cumc";

    @Autowired
    public Extractor(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }

    public void start() {
        log.info("start to exact file ...");
        File file = new File(RootDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("failed to create root dir");
                return;
            }
        }
        extractMapper();

        DateTime dateTime = DateTime.now();
        String zipFileName = "/tmp/rascal_to_cumc_" + dateTime.toString("yyyyMMdd") + ".zip";
        RascalZipper zipper = new RascalZipper(RootDir, zipFileName);
        try {
            zipper.zipFiles();
        } catch (IOException e) {
            log.error("caught: ", e);
        }
    }

    private void extractMapper() {
        List<Standalone> list = jdbcTemplate.query(SQL_STANDALONE_PROTOCOL, new StandaloneRowMapper());
        for (Standalone s : list) {
            String dirName = String.format("%s_Y%02d_M%02d", s.protocolNumber, s.protocolYear, s.modificationNumber);
            String folder = RootDir+"/"+dirName + "/" + "ATTACHED_STANDALONE_PROTOCOLS";
            folder(folder);
            String fileName = folder + "/" + s.fileName;
            blobToFile(fileName, s.bytes);
        }
    }

    private void folder(String dir) {
        File file = new File(dir);
        if( !file.exists() ) {
            file.mkdirs();
        }
    }

    private void blobToFile(String fileName, byte[] blob) {
        try {
            OutputStream os = new FileOutputStream(new File(fileName));
            FileCopyUtils.copy(blob, os);
            os.close();
        } catch (Exception e) {
            log.error("caught: ", e);
        }
    }

}