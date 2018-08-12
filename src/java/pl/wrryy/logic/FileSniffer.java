package pl.wrryy.logic;

import java.io.File;
import java.util.Objects;

public class FileSniffer {

    /**
     * Just a wrapper.
     *
     * @param folder will search for files inside
     */
    public void run(File folder) {
        readFolder(folder);
    }

    /**
     * Loops through folders and sniffs for files.
     *
     * @param folder will search for files inside
     */
    private void readFolder(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                readFolder(file);
            } else {
                //TODO wielowatkowo ~5 doRename'ow naraz
                try {
                    FileModifier renamer = FileModifier.getInstance(file);
                    renamer.workTheFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
