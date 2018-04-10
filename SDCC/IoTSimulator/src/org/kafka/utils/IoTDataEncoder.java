package org.kafka.utils;

import Model.IoTData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

public class IoTDataEncoder implements Encoder<IoTData> {
    private static final Logger logger = Logger.getLogger(IoTDataEncoder.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public IoTDataEncoder(VerifiableProperties verifiableProperties) {
    }

    public byte[] toBytes(IoTData iotEvent) {
        try {
            String msg = objectMapper.writeValueAsString(iotEvent);
            logger.info(msg);
            return msg.getBytes();
        } catch (JsonProcessingException var3) {
            logger.error("Error in Serialization", var3);
            return null;
        }
    }
}
