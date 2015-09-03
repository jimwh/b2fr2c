package edu.columbia.rascal.cumc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StandaloneRowMapper implements RowMapper<Standalone> {

    @Override
    public Standalone mapRow(ResultSet resultSet, int rownum) throws SQLException {
        Blob blob = resultSet.getBlob("DOCUMENTDATASTAMPED");
        if( blob == null ) {
            blob = resultSet.getBlob("DOCUMENTDATA");
            if( blob == null )
                throw new SQLException("foo barr...");
        }
        byte[] bytes = new byte[(int)blob.length()];
        InputStream in = blob.getBinaryStream();
        try {
            while( in.read(bytes) != -1){}
        } catch (IOException e) {
            throw new SQLException("caught: ", e);
        }
        return new Standalone(
                resultSet.getString("PROTOCOLNUMBER"),
                resultSet.getInt("PROTOCOLYEAR"),
                resultSet.getInt("MODIFICATIONNUMBER"),
                resultSet.getString("FILENAME"),
                bytes
                );
    }
}
