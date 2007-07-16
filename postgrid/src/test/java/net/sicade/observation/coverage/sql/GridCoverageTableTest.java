/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2007, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sicade.observation.coverage.sql;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import net.sicade.observation.CatalogException;
import net.sicade.observation.coverage.CoverageReference;
import net.sicade.observation.sql.DatabaseTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link GridCoverageTable}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class GridCoverageTableTest extends DatabaseTest {
    /**
     * The name of the coverage to be tested.
     */
    public static final String SAMPLE_NAME = "198602";

    /**
     * The format for the date used in this test.
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Tests the {@link GridCoverageTable#getEntry} and {@link GridCoverageTable#getEntries} methods.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException, ParseException {
        testConnection(); // TODO: remove this test once 'Database.isSpatialEnabled()' connect itself.

        final GridCoverageTable table = new GridCoverageTable(database);
        table.setLayer(new LayerEntry(LayerTableTest.SAMPLE_NAME, null, null, 1, null));
        final CoverageReference entry = table.getEntry(SAMPLE_NAME);
        assertEquals(SeriesTableTest.SAMPLE_NAME + ':' + SAMPLE_NAME, entry.getName());
        assertSame(entry, table.getEntry(SAMPLE_NAME));

        table.setTimeRange(date("1985-08-01"), date("1985-08-15"));
        final CoverageReference byRange = table.getEntry();

//        final Set<CoverageReference> entries = table.getEntries();
//        assertFalse(entries.isEmpty());
//        assertTrue(entries.contains(entry));
    }

    /**
     * Returns the specified date.
     */
    private static Date date(final String asText) throws ParseException {
        return DATE_FORMAT.parse(asText);
    }
}
