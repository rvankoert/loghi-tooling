package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class TextLineElementSerializer extends
        JsonSerializer<List<TextLineElement>> {

    @Override
    public void serialize(List<TextLineElement> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
//        jgen.writeObjectField("test", "test");
        for (TextLineElement me : value) {
            provider.defaultSerializeField(me.getType(), me, jgen);
        }
        jgen.writeEndObject();

    }


}