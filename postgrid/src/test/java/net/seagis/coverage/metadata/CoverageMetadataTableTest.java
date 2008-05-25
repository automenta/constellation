/*
 * Ecocast - NASA Ames Research Center
 * (C) 2008, Ecocast
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
package net.seagis.coverage.metadata;

import java.util.Set;
import java.util.Date;
import java.sql.SQLException;

import org.geotools.util.NumberRange;
import net.seagis.catalog.TableTest;
import net.seagis.catalog.CatalogException;

import org.junit.*;


/**
 * Tests {@link CoverageMetadataTable}.
 *
 * @version $Id$
 * @author Sam Hiatt
 */
public class CoverageMetadataTableTest extends TableTest {
    /**
     * The name of the layer metadata set to be tested.
     */
    public static final String id = "test                                                                                                                                                                                                    ";
    /**
     * Tests the {@link LayerTableTest#getIdentifiers} method.
     */
    @Test
    public void testIdentifiers() throws CatalogException, SQLException {
        final CoverageMetadataTable table = new CoverageMetadataTable(database);
        final Set<String> identifiers = table.getIdentifiers();
        assertTrue(identifiers.contains(id));
    }

    /**
     * Tests the {@link LayerTableTest#getEntry} and @link LayerTableTest#getEntries} methods.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException {
        final CoverageMetadataTable table = new CoverageMetadataTable(database);
        final CoverageMetadata entry = table.getEntry(id);
        //assertEquals(id, entry.getName());
        assertSame("Should be cached.", entry, table.getEntry(id));

        final Set<CoverageMetadata> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
        
        System.out.println(entry.getMetadata());

    }

}
