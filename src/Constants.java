public class Constants {
    static final String common = System.getProperty("user.dir") + "/src/Common.cfg";
    static final String peers = System.getProperty("user.dir") + "/src/PeerInfo.cfg";

    private static int NumberOfPreferredNeighbors;
    private static int UnchokingInterval;
    private static int OptimisticUnchokingInterval;
    private static String FileName;
    private static int FileSize;
    private static int PieceSize;

    public static int getNumberOfPreferredNeighbors() {
        return NumberOfPreferredNeighbors;
    }

    public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
        NumberOfPreferredNeighbors = numberOfPreferredNeighbors;
    }

    public static int getUnchokingInterval() {
        return UnchokingInterval;
    }

    public static void setUnchokingInterval(int unchokingInterval) {
        UnchokingInterval = unchokingInterval;
    }

    public static int getOptimisticUnchokingInterval() {
        return OptimisticUnchokingInterval;
    }

    public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
        OptimisticUnchokingInterval = optimisticUnchokingInterval;
    }

    public static String getFileName() {
        return FileName;
    }

    public static void setFileName(String fileName) {
        FileName = fileName;
    }

    public static int getFileSize() {
        return FileSize;
    }

    public static void setFileSize(int fileSize) {
        FileSize = fileSize;
    }

    public static int getPieceSize() {
        return PieceSize;
    }

    public static void setPieceSize(int pieceSize) {
        PieceSize = pieceSize;
    }
}
