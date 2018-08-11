package pl.wrryy;

import pl.wrryy.logic.FileSniffer;
import pl.wrryy.logic.FileRenamer;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileSniffer sniffer = new FileSniffer();
        String path = "C:\\Users\\wojti\\Downloads\\zdj\\1.jpg";
//         String path = "C:\\Users\\wojti\\Downloads\\mov\\1 (1).mov";

        sniffer.run(new File(path));

//        Metadata metadata = null;
//        try {
//            metadata = ImageMetadataReader.readMetadata(new File(path));
//        } catch (ImageProcessingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Directory directory = metadata.getFirstDirectoryOfType(Directory.class);
//        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//        System.out.println(date);
//        FileRenamer.getEXIF(new File(path));
    }
}
