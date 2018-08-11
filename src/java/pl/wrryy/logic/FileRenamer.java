package pl.wrryy.logic;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileRenamer {
    private DataParser parser = new DataParser();
    private static final List<String> badExtensions = Arrays.asList("db", "gif");

    /**
     * Changes file's name to contain its creation datetime
     *
     * @param file file to rename
     */
    void rename(File file) {
        String fileExt = parser.getFileExt(file);
        if (!badExtensions.contains(fileExt)) {
            String newPath = buildNewPath(file);
            try {
                Path renamedPath = Files.move(file.toPath(), Paths.get(newPath), REPLACE_EXISTING);
                if (renamedPath.equals(file.toPath())) {
                    System.out.println(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildNewPath(File file) {
        return file.getParent() + "\\" +
                    parser.getFileDatetime(file) + "." +
                    parser.getFileExt(file);
    }


}

