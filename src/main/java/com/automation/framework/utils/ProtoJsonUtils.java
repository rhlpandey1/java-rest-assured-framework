package com.automation.framework.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

/**
 * Bridge between Protobuf-generated messages and the JSON wire format
 * used by the REST services under test.
 *
 * <p>The printer uses {@code includingDefaultValueFields()} so that
 * explicitly-set defaults (empty strings, zero ids) survive serialization,
 * which is what most REST gateways expect when creating resources.</p>
 *
 * <p>The parser uses {@code ignoringUnknownFields()} so responses with
 * extra fields (gateways, mock servers like jsonplaceholder) don't break
 * deserialization.</p>
 */
public final class ProtoJsonUtils {

    private static final JsonFormat.Printer PRINTER = JsonFormat.printer()
            .omittingInsignificantWhitespace();

    private static final JsonFormat.Parser PARSER = JsonFormat.parser()
            .ignoringUnknownFields();

    private ProtoJsonUtils() {}

    /** Serialize a protobuf message to its canonical JSON representation. */
    public static String toJson(MessageOrBuilder message) {
        try {
            return PRINTER.print(message);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException("Failed to serialize " + message.getClass().getSimpleName(), e);
        }
    }

    /** Populate the supplied builder from a JSON string and return it. */
    public static <B extends Message.Builder> B fromJson(String json, B builder) {
        try {
            PARSER.merge(json, builder);
            return builder;
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException("Failed to deserialize into " + builder.getClass().getSimpleName(), e);
        }
    }
}
