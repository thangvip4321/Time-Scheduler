package entities.beanSerializerDeserializer;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This class is basically helping reduce the complex implementation 
 * 
 * @author Nguyen Tuan Ngoc  
 */
public class JsonUtils {
    
    public static ObjectMapper objectMapper = null;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule s = new SimpleModule();
        s.addSerializer(Timestamp.class, new TimestampSerializerTypeHandler());
        s.addDeserializer(Timestamp.class, new TimestampDeserializerTypeHandler());
        objectMapper.registerModule(s);
    };
}