package nl.knaw.huc.di.images.loghiwebservice.resources;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.util.List;
import java.util.Map;

public class FormMultipartHelper {
    public static <T> T getFieldOrDefaultValue(Class<T> type, FormDataMultiPart multiPart, Map<String, List<FormDataBodyPart>> fields, String fieldName, T defaultValue) {
        if (fields.containsKey(fieldName)) {
            return multiPart.getField(fieldName).getValueAs(type);
        } else {
            return defaultValue;
        }
    }
}