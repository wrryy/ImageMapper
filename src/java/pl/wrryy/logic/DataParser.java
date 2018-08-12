package pl.wrryy.logic;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class with the intention of parsing data from files.
 */
public class DataParser {

    private final File file;
    private Optional<Metadata> metadata; //FIXME cos z tym
    private static final String DATE_FORMAT = "yyyy-MM-dd HH.mm.ss";

    public DataParser(File file) {
        this.file = file;
        setMetadata();
    }

    private void setMetadata() {
        metadata = Optional.empty();
        try {
            metadata = Optional.of(ImageMetadataReader.readMetadata(file));
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return file's creation datetime
     */
    private String getDatetime() {
        String dateTime;
        if (metadata.isPresent()) {
            Directory directory = metadata.get().getFirstDirectoryOfType(FileSystemDirectory.class);
            Date date = directory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
            dateTime = new SimpleDateFormat(DATE_FORMAT).format(date);
        } else {
            dateTime = parseDatetime(file);
        }
        return dateTime;
    }

    String getGPSData() { //TODO jakich danych wgl potrzebuje?
        String gpsData = "";
//        if (metadata.isPresent()) {
//            Directory directory = metadata.get().getFirstDirectoryOfType(GpsDirectory.class);
//            Date date = directory.getDate(GpsDirectory.);
//            gpsData = new SimpleDateFormat(DATE_FORMAT).format(date);
//        } else {
//            gpsData = parseDatetime(file);
//        }
        return gpsData;
    }

    /**
     * Parses file's name for extension
     *
     * @return file's extension
     */
    String getFileExt() {
        String extension = "err";
        try {
            extension = file.getName().split(".*\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Failed parsing file extension :: " + file.getName());
        }
        return extension;
    }

    private String parseDatetime(File file) {
        String dateString = null;
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
            MappedByteBuffer buffer = accessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            int c = 0;
            int size = 200;
            byte[] s = new byte[size];
            int restrict = 0;
            Pattern patternFull = Pattern.compile("(\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2})"); //FIXME wiecej paternow?
//            try {
            for (int i = 0; i < file.getTotalSpace() - 2; i++) {
                s[c] = buffer.get(i);
                c++;
                if (c == size) {
                    String part = new String(s, Charset.forName("UTF-8"));
                    System.out.println(part);
                    Matcher matcher = patternFull.matcher(part);
                    if (matcher.find()) {
                        System.out.println(matcher.group(1));
                        return matcher.group(1);
                    }
                    s = new byte[size];
                    i -= 20;
                    c = 0;
                    restrict++;
                }
                //safety break || tylko odnosnie zdjec
                if (restrict == 3) {
                    break;
                }
            }
        } catch (Exception e) { //FIXME zapewne wypadaloby obluzyc kazdy w konkretny sposob?
            e.printStackTrace();
        }
        return dateString;
    }


    Path getFilePath() {
        return file.toPath();
    }

    /**
     * Return updated path with new file name
     *
     * @return modified file's (@<code>Path</code>) with changed name
     */
    Path getNewPath() {
        return Paths.get(
                file.getParent() +
                        getDatetime() +
                        getFileExt());
    }
}
