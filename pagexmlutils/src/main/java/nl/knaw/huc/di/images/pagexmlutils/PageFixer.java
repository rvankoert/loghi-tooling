package nl.knaw.huc.di.images.pagexmlutils;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import org.primaresearch.dla.page.io.FileInput;

import java.io.File;
import java.io.IOException;

public class PageFixer {

    public static void fix(File file, String namespace) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                fix(subFile, namespace);
            }
        } else {
            if (file.getName().endsWith(".xml")) {
                FileInput fileInput = new FileInput(file);
                System.out.println("fixing file: " + file.getName());
                PcGts page = PageUtils.readPageFromFile(file.toPath());
                PageUtils.writePageToFile(page, namespace, file.toPath());
            }
        }

    }

    public static String fix(String pageString, String namespace) throws IOException {
        PcGts page = PageUtils.readPageFromString(pageString);
        return PageUtils.convertPcGtsToString(page, namespace);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("file does not exist");
                return;
            }
            String namespace = PageUtils.NAMESPACE2019;
            if (args.length > 1) {
                namespace = args[1];
            }

            fix(file, namespace);
        } else {
            System.err.println("Please provide a directory containing pagexml or a pagexml file");
        }
    }
}
