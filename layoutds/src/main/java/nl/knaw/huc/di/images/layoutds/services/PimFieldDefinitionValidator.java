package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldDefinition;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class PimFieldDefinitionValidator {
    public static void validatePimField(Set<String> errors, PimFieldDefinition field) {
        switch (field.getType()) {
            case text:
                break;
            case checkbox:
                if (!"true".equalsIgnoreCase(field.getValue()) && "false".equalsIgnoreCase(field.getValue())) {
                    errors.add(String.format("\"%s\" is not a valid value for \"%s\"", field.getValue(), field.getName()));
                }
                break;
            case date:
                break;
            case documentimage:
                break;
            case numeric:
                try {
                    if (StringUtils.isBlank(field.getValue())) {
                        field.setValue("0");
                    }
                    Integer.parseInt(field.getValue());
                } catch (NumberFormatException e) {
                    errors.add(String.format("\"%s\" is not a valid value for \"%s\"", field.getValue(), field.getName()));
                }
                break;
            case pimuser:
                break;
            case script:
                break;
            case select:
                break;
            case notes:
                break;
        }
    }

    public static void validatePimFieldValue(Set<String> errors, PimFieldValue pimFieldValue) {
        final PimFieldDefinition field = pimFieldValue.getField();
        switch (field.getType()) {
            case text:
                break;
            case checkbox:
                if (!"true".equalsIgnoreCase(pimFieldValue.getValue()) && !"false".equalsIgnoreCase(pimFieldValue.getValue())) {
                    errors.add(String.format("\"%s\" is not a valid value for \"%s\"", pimFieldValue.getValue(), field.getName()));
                }
                break;
            case date:
                break;
            case documentimage:
                break;
            case numeric:
                try {
                    if (StringUtils.isBlank(pimFieldValue.getValue())) {
                        pimFieldValue.setValue("0");
                    }
                    Integer.parseInt(pimFieldValue.getValue());
                } catch (NumberFormatException e) {
                    errors.add(String.format("\"%s\" is not a valid value for \"%s\"", field.getValue(), field.getName()));
                }
                break;
            case pimuser:
                break;
            case script:
                break;
            case select:
                break;
            case notes:
                break;
        }
    }
}
