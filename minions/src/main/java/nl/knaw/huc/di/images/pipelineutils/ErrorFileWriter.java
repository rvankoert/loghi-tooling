package nl.knaw.huc.di.images.pipelineutils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ErrorFileWriter {
    private final String outputBase;

    public ErrorFileWriter(String outputBase) {

        this.outputBase = outputBase;
    }

    public void write(String identifier, Exception exception, String message) {
        final String errorFileName = identifier + ".error";

        final List<String> errors = List.of(message, exception.getMessage());

        try {
            Files.write(Paths.get(outputBase, errorFileName), errors);
        } catch (IOException e) {
            LoggerFactory.getLogger(ErrorFileWriter.class).error("Could not write error file: {}", errorFileName);
        }
    }

    public void write(String identifier, String message) {
        final String errorFileName = identifier + ".error";

        try {
            Files.write(Paths.get(outputBase, errorFileName), List.of(message));
        } catch (IOException e) {
            LoggerFactory.getLogger(ErrorFileWriter.class).error("Could not write error file: {}", errorFileName);
        }
    }

    public void writeToFile(String errorFileName, String message) {
        try {
            Files.write(Paths.get(errorFileName), List.of(message));
        } catch (IOException e) {
            LoggerFactory.getLogger(ErrorFileWriter.class).error("Could not write error file: {}", errorFileName);
        }
    }
}
