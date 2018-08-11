package pl.wrryy.logic;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class with the intention of parsing data from files.
 */
class DataParser {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH.mm.ss";

    /**
     * @param file file to extract datetime from
     * @return file creation's datetime
     */
    String getFileDatetime(File file) {
        String dateString = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            dateString = new SimpleDateFormat(DATE_FORMAT).format(date);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(" IOExc :: " + file.getName());
        } catch (NullPointerException e) {
            System.out.println("Failed to extract date from :: " + file.getName());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (dateString == null) {
            dateString = parseFileForData(file);
        }
        return dateString;
    }

    /**
     * Parses file's name for extension
     *
     * @param file file to extract extension from
     * @return file's extension
     */
    String getFileExt(File file) {
        String n = null;
        try {
            n = file.getName().split(".*\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Failed parsing file extension :: " + file.getName());
        }
        return n;
    }

    String getEXIF(File file) {
        String dateString = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            Directory directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
            System.out.println(directory.getDate(QuickTimeDirectory.TAG_CREATION_TIME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    private String parseFileForData(File file) {
        String dateString = null;
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
            MappedByteBuffer buffer = accessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            int c = 0;
            int size = 200;
            int restrict = 0;
            byte[] s = new byte[size];
            Pattern patternFull = Pattern.compile("(\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2})");
            Pattern pattern1 = Pattern.compile("(\\d{4})");
            try {

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
                    //safety break || tylko odnosnie zdjec TODO dorobic oddzielny kod dla filmow
                    if (restrict == 3) {
                        break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
