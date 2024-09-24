package org.example.grpc.contract.utils;

import com.google.protobuf.FloatValue;
import com.google.protobuf.StringValue;

/*
 * Utility class for converting values to protobuf types. Not null safe yet.
 */
public class ProtoUtils {

    /**
     * Converts a string to a StringValue.
     *
     * @param value the string to convert
     * @return the converted StringValue
     */
    public static StringValue convertToStringValue(String value) {
        return StringValue.newBuilder()
                .setValue(value)
                .build();
    }

    /**
     * Converts a float to a FloatValue.
     *
     * @param value the float to convert
     * @return the converted FloatValue
     */
    public static FloatValue convertToFloatValue(float value) {
        return FloatValue.newBuilder()
                .setValue(value)
                .build();
    }
}
