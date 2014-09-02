/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constellation.json.metadata;

import java.util.Arrays;
import java.io.IOException;


/**
 * A path together with an index for each component in the path.
 * This is used as keys for storing metadata values in a map.
 *
 * <p><b>Implementation note:</b>
 * We need the full path, not only the last UML identifier, because the same metadata object could be reached
 * by different paths (e.g. {@code "contact.party"} and {@code "identificationInfo.pointOfContact.party"}).</p>
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class NumerotedPath {
    /**
     * The path elements.
     */
    final String[] path;

    /**
     * The index of each elements in the path.
     */
    final int[] indices;

    /**
     * Creates a new key for the given node and indices.
     *
     * @param path    The template containing the path.
     * @param indices The index of each elements in the path. This array will be partially copied.
     */
    NumerotedPath(final String[] path, final int[] indices) {
        this.path     = path;
        this.indices  = Arrays.copyOfRange(indices, 0, path.length);
    }

    /**
     * Returns a hash code value for this key.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(path) ^ Arrays.hashCode(indices);
    }

    /**
     * Returns {@code true} if the given key is equals to the given object.
     */
    @Override
    public boolean equals(final Object other) {
        return (other instanceof NumerotedPath) &&
               Arrays.equals(path,    ((NumerotedPath) other).path) &&
               Arrays.equals(indices, ((NumerotedPath) other).indices);
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        try {
            formatPath(buffer, path, 0, indices);
        } catch (IOException e) {
            throw new AssertionError(e); // Should never happen, since we are writting to a StringBuilder.
        }
        return buffer.toString();
    }

    /**
     * Formats the given path, without quotes.
     */
    static void formatPath(final Appendable out, final CharSequence[] path, int pathOffset, final int[] indices) throws IOException {
        while (pathOffset < path.length) {
            if (pathOffset != 0) {
                out.append(Keywords.PATH_SEPARATOR);
            }
            out.append(path[pathOffset]);
            if (indices != null) {
                final int index = indices[pathOffset];
                if (index != 0) {
                    out.append('[').append(Integer.toString(index)).append(']');
                }
            }
            pathOffset++;
        }
    }
}
