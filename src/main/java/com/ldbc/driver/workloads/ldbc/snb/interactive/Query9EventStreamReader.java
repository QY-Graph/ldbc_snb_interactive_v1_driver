package com.ldbc.driver.workloads.ldbc.snb.interactive;


import com.ldbc.driver.Operation;
import com.ldbc.driver.generator.CsvEventStreamReaderBasicCharSeeker;
import com.ldbc.driver.generator.GeneratorException;
import com.ldbc.driver.util.csv.CharSeeker;
import com.ldbc.driver.util.csv.Extractors;
import com.ldbc.driver.util.csv.Mark;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

public class Query9EventStreamReader implements Iterator<Operation<?>> {
    private final Iterator<Object[]> csvRows;

    public Query9EventStreamReader(Iterator<Object[]> csvRows) {
        this.csvRows = csvRows;
    }

    @Override
    public boolean hasNext() {
        return csvRows.hasNext();
    }

    @Override
    public Operation<?> next() {
        Object[] rowAsObjects = csvRows.next();
        return new LdbcQuery9(
                (long) rowAsObjects[0],
                null,
                (Date) rowAsObjects[1],
                LdbcQuery9.DEFAULT_LIMIT
        );
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(String.format("%s does not support remove()", getClass().getSimpleName()));
    }

    public static class Query9Decoder implements CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]> {
        /*
        Person|Date0
        14293655512417|1339632000
        */
        @Override
        public Object[] decodeEvent(CharSeeker charSeeker, Extractors extractors, int[] columnDelimiters, Mark mark) throws IOException {
            long personId;
            if (charSeeker.seek(mark, columnDelimiters)) {
                personId = charSeeker.extract(mark, extractors.long_()).longValue();
            } else {
                // if first column of next row contains nothing it means the file is finished
                return null;
            }

            Date date;
            if (charSeeker.seek(mark, columnDelimiters)) {
                date = new Date(charSeeker.extract(mark, extractors.long_()).longValue());
            } else {
                throw new GeneratorException("Error retrieving date");
            }

            return new Object[]{personId, date};
        }
    }
}
