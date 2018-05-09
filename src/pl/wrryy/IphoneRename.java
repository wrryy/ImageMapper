package pl.wrryy;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

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

public class IphoneRename {

    static final String DATE_FORMAT = "yyyy-MM-dd HHmmss";

    /**
     * Loops through the folders
     *
     * @param folder
     */
    static void readFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                readFolder(file);
            } else {
                rename(file);
            }
        }
    }

    /**
     * Changes file's name to contain its creation datetime
     *
     * @param file
     */
    private static void rename(File file) {
        String fileExt = getFileExt(file);
        if (!"db".equals(fileExt) && !"gif".equals(fileExt)) {
            String pathName = buildNewPath(file);
            File temp = new File(pathName);
            boolean renamed = file.renameTo(temp);
            if (!renamed) {
                System.out.println(file.getName());
//                rename(file);
            }
        }
    }

    /**
     * Parses file's name for extension
     *
     * @param file
     * @return file's extension
     */
    private static String getFileExt(File file) {
        String n = null;
        try {
            n = file.getName().split(".*\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Failed parsing file extension :: " + file.getName());
        }
        return n;
    }

    /**
     * @param file
     * @return file creation's date
     */
    public static String getFileDate(File file) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (dateString == null) {
            dateString = parseFileForData(file);
        }
        if (dateString == null) {
            dateString = parseFileForData(file);
        }
        return dateString;
    }

    public static String getEXIF(File file) {
          String dateString = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            System.out.println(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
            System.out.println(directory.getDate(ExifSubIFDDirectory.TAG_TIME_ZONE_OFFSET));
            System.out.println(directory.getDate(ExifSubIFDDirectory.TAG_TIME_ZONE_OFFSET_TIFF_EP));
//            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//            dateString = new SimpleDateFormat(DATE_FORMAT).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

        private static String parseFileForData (File file){
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
                        //safety break
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
        private static String buildNewPath (File file){
            StringBuilder sb = new StringBuilder();
            return sb.append(file.getParent())
                    .append("\\")
                    .append(getFileDate(file))
                    .append(".")
                    .append(getFileExt(file))
                    .toString();
        }
    }

