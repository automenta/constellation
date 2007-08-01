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
package net.sicade.coverage.catalog.sql;

import java.util.Set;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.SQLException;
import net.sicade.catalog.CatalogException;
import net.sicade.coverage.catalog.Layer;
import net.sicade.catalog.DatabaseTest;
import net.sicade.coverage.catalog.CoverageReference;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link LayerTable}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class LayerTableTest extends DatabaseTest {
    /**
     * The name of the layer to be tested.
     */
    public static final String SAMPLE_NAME = "SST (Monde - hebdomadaires)";

    /**
     * The start time, end time, and a sample time between them.
     */
    public static final Date START_TIME, END_TIME, SAMPLE_TIME;
    static {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            START_TIME  = format.parse("1986-01-05");
            SAMPLE_TIME = format.parse("1986-01-13");
            END_TIME    = format.parse("1986-01-20");
        } catch (ParseException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Tests the {@link LayerTableTest#getEntry} and @link LayerTableTest#getEntries} methods.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException {
        final LayerTable table = new LayerTable(database);
        table.setTimeRange(START_TIME, END_TIME);
        final Layer entry = table.getEntry(SAMPLE_NAME);
        assertEquals(SAMPLE_NAME, entry.getName());
        assertSame("Should be cached.", entry, table.getEntry(SAMPLE_NAME));
        assertEquals(START_TIME, entry.getTimeRange().getMinValue());
        assertEquals(END_TIME,   entry.getTimeRange().getMaxValue());

        final Set<Layer> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));

        final Set<Date> availableTimes = entry.getAvailableTimes();
        assertEquals(3, availableTimes.size());
        final Set<CoverageReference> references = entry.getCoverageReferences();
        assertEquals(3, references.size());
        final CoverageReference reference = entry.getCoverageReference(SAMPLE_TIME, null);
        assertTrue(references.contains(reference));
        assertSame("Should be cached.", availableTimes, entry.getAvailableTimes());
    }
}
