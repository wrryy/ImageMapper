package pl.wrryy;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IphoneRename {

    static final String DATE_FORMAT = "yyyy-MM-dd HHmmss";

    static void readFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                readFolder(file);
            } else {
                rename(file);
            }
        }
    }

    static void rename(File file) {
        String fileExt = getFileExt(file);
        if (!fileExt.equals("db")) {
            StringBuilder sb = new StringBuilder();
            String pathName = sb.append(file.getParent()).append("\\").append(getFileDate(file)).append(".").append(getFileExt(file)).toString();
            File temp = new File(pathName);
            file.renameTo(temp);
        }
    }

    static String getFileExt(File file) {
        String n = null;
        try {
            n = file.getName().split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Failed parsing file extension :: " + file.getName());
        }
        return n;
    }

    static String getFileDate(File file) {
        String sdf = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            sdf = new SimpleDateFormat(DATE_FORMAT).format(date);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(" IOExc :: " + file.getName());
        } catch (NullPointerException e) {
            System.out.println("Failed to extract date from :: " + file.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sdf;
    }
}

