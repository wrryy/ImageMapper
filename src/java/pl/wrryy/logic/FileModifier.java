package pl.wrryy.logic;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

class FileModifier {
    private DataParser parser;
    private static final List<String> badExtensions = Arrays.asList("db", "gif");
    private static final List<String> movieExtensions = Arrays.asList("mov", "mp4");

    static FileModifier getInstance(File file) {
        return new FileModifier(file);
    }

    private FileModifier(File file) {
        this.parser = new DataParser(file);
    }

    /**
     * Changes file's name to contain its creation datetime
     */
    void workTheFile() {
        String fileExt = parser.getFileExt().toLowerCase();
        if (!badExtensions.contains(fileExt)) {
            Path renamed = rename();
//                if(!parser.getNewPath().equals(renamed)){
            //TODO loger
//                }
            if (!movieExtensions.contains(fileExt)) { // movies don't have geotags
                parser.getGPSData();
            }
        }
    }

    private Path rename() {
        Path path = Paths.get("");
        try {
            Files.move(parser.getFilePath(), parser.getNewPath(), REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}

