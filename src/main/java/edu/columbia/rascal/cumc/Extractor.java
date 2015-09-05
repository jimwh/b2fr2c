package edu.columbia.rascal.cumc;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Extractor {

    private static final Logger log = LoggerFactory.getLogger(Extractor.class);
    private final JdbcTemplate jdbcTemplate;
    public static final String RootDirectory = File.separator + "tmp" +File.separator + "rascal_to_cumc";

    @Autowired
    public Extractor(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }

    public void start() {
        log.info("start to exact file ...");
        File file = new File(RootDirectory);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("failed to create root dir");
                return;
            }
        }
        //
        jdbcTemplate.query(ExtractorStandaloneProtocol.SQL_STANDALONE_PROTOCOL,
                new ExtractorStandaloneProtocol());
        //
        DateTime dateTime = DateTime.now();
        String zipFileName = RootDirectory + "_" + dateTime.toString("yyyyMMdd") + ".zip";
        RascalZipper zipper = new RascalZipper(RootDirectory, zipFileName);
        try {
            zipper.zipFiles();
        } catch (IOException e) {
            log.error("caught: ", e);
        }
    }

}