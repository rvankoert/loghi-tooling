package nl.knaw.huc.di.images.layoutds.jpahelpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

// inspired by https://www.baeldung.com/hibernate-persist-json-object
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    public static final Logger LOG = LoggerFactory.getLogger(HashMapConverter.class);
    private final ObjectMapper objectMapper;

    public HashMapConverter() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        String dbData = null;
        try {
            dbData = objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            LOG.error("JSON writing error", e);
        }

        return dbData;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        Map<String, Object> map = null;
        if (dbData != null) {
            try {
                map = objectMapper.readValue(dbData, Map.class);
            } catch (final IOException e) {
                LOG.error("JSON reading error", e);
            }
        }
        return map;
    }
}

