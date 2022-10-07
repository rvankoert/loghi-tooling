package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import org.primaresearch.dla.page.io.FileInput;

import java.io.File;
import java.io.IOException;

public class PageFixer {

    public static void fix(File file) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                fix(subFile);
            }
        } else {
            if (file.getName().endsWith(".xml")) {
                FileInput fileInput = new FileInput(file);
                System.out.println("fixing file: " + file.getName());
                PcGts page = PageUtils.readPageFromFile(file.toPath());
                PageUtils.writePageToFile(page, file.toPath());
            }
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("file does not exist");
                return;
            }
            fix(file);
        } else {
            System.err.println("Please provide a directory containing pagexml or a pagexml file");
        }
    }
}
