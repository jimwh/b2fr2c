package edu.columbia.rascal.cumc;

public class Standalone {
    public final String protocolNumber;
    public final int protocolYear;
    public final int modificationNumber;
    public final String fileName;
    public final byte[] bytes;

    public Standalone(String n, int y, int m, String f, byte[] b) {
        protocolNumber = n;
        protocolYear = y;
        modificationNumber = m;
        fileName = f;
        bytes = b;
    }
}
