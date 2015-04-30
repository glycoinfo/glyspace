package org.glyspace.registry.view.conversion;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class DateSerializer extends JsonSerializer<Date> {
	
	@Override
    public void serialize(Date value, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,JsonProcessingException {
		jgen.writeString(new ISO8601DateFormat().format(value));
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }
}
