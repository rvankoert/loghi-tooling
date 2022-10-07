package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldDefinition;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SiameseLabelGenerator {
    private final FieldValueRetriever fieldValueRetriever;

    public SiameseLabelGenerator(FieldValueRetriever fieldValueRetriever) {

        this.fieldValueRetriever = fieldValueRetriever;
    }

    public String generateLabel(String imageRemoteUri, List<PimFieldDefinition> pimFields) {
        return pimFields.stream().map(field -> getLabel(imageRemoteUri, field))
                .filter(Objects::nonNull)
                .collect(Collectors.joining("_"));


    }

    private String getLabel(String imageRemoteUri, PimFieldDefinition pimFieldDefinition) {
        final String label = fieldValueRetriever.getFieldValueFor(imageRemoteUri, pimFieldDefinition);

        if (label != null) {
            return label.replaceAll("[^A-Za-z0-9]", "_");
        }
        return null;
    }

    public interface FieldValueRetriever {
        String getFieldValueFor(String imageRemoteUri, PimFieldDefinition pimField);
    }
}
