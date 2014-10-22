package com.ldbc.driver.workloads.ldbc.snb.interactive;


import com.google.common.collect.Lists;
import com.ldbc.driver.Operation;
import com.ldbc.driver.generator.CsvEventStreamReader_OLD;
import com.ldbc.driver.generator.GeneratorException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;

public class Query10EventStreamReader_OLD implements Iterator<Operation<?>> {
    public static final String PERSON_ID = "PersonID";
    public static final String PERSON_URI = "PersonURI";
    public static final String MONTH = "HS0";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CsvEventStreamReader_OLD<Operation<?>> csvEventStreamReaderOLD;

    public static final CsvEventStreamReader_OLD.EventDecoder<Operation<?>> EVENT_DECODER = new CsvEventStreamReader_OLD.EventDecoder<Operation<?>>() {
        @Override
        public boolean eventMatchesDecoder(String[] csvRow) {
            return true;
        }

        @Override
        public Operation<?> decodeEvent(String[] csvRow) {
            String eventParamsAsJsonString = csvRow[0];
            JsonNode params;
            try {
                params = objectMapper.readTree(eventParamsAsJsonString);
            } catch (IOException e) {
                throw new GeneratorException(String.format("Error parsing JSON event params\n%s", eventParamsAsJsonString), e);
            }
            long personId = params.get(PERSON_ID).asLong();
            String personUri = params.get(PERSON_URI).asText();
            int month1 = params.get(MONTH).asInt();
            return new LdbcQuery10(personId, personUri, month1, LdbcQuery10.DEFAULT_LIMIT);
        }
    };

    public Query10EventStreamReader_OLD(Iterator<String[]> csvRowIterator) {
        this(csvRowIterator, CsvEventStreamReader_OLD.EventReturnPolicy.AT_LEAST_ONE_MATCH);
    }

    public Query10EventStreamReader_OLD(Iterator<String[]> csvRowIterator, CsvEventStreamReader_OLD.EventReturnPolicy eventReturnPolicy) {
        Iterable<CsvEventStreamReader_OLD.EventDecoder<Operation<?>>> decoders = Lists.newArrayList(EVENT_DECODER);
        CsvEventStreamReader_OLD.EventDescriptions<Operation<?>> eventDescriptions = new CsvEventStreamReader_OLD.EventDescriptions<>(decoders, eventReturnPolicy);
        this.csvEventStreamReaderOLD = new CsvEventStreamReader_OLD<>(csvRowIterator, eventDescriptions);
    }

    @Override
    public boolean hasNext() {
        return csvEventStreamReaderOLD.hasNext();
    }

    @Override
    public Operation<?> next() {
        return csvEventStreamReaderOLD.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(String.format("%s does not support remove()", getClass().getSimpleName()));
    }
}
