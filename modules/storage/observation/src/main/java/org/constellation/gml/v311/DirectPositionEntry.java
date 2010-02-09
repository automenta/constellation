/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.gml.v311;

import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DirectPositionEntry {

    private DirectPositionType position;

    private String name;

    public DirectPositionEntry(String name, DirectPositionType position) {
        this.name     = name;
        this.position = position;
    }

    /**
     * @return the position
     */
    public DirectPositionType getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(DirectPositionType position) {
        this.position = position;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return a description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DirectPositionEntry]:");
        if (name != null) {
            s.append("Name = ").append(name).append('\n');
        }
        if (position != null) {
            s.append("position = ").append(position).append('\n');
        }
        return s.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DirectPositionEntry) {
            final DirectPositionEntry that = (DirectPositionEntry) object;
            return  Utilities.equals(this.name, that.name)     &&
                    Utilities.equals(this.position, that.position);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
