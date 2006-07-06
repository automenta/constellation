/*
 * Sicade - Syst�mes int�gr�s de connaissances pour l'aide � la d�cision en environnement
 * (C) 2006, Institut de Recherche pour le D�veloppement
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
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sicade.observation.fishery.sql;

// Sicade dependencies
import net.sicade.observation.sql.ProcedureEntry;
import net.sicade.observation.fishery.FisheryType;


/**
 * Impl�mentation d'une entr�e repr�sentant un {@linkplain FisheryType type de p�che}.
 * 
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class FisheryTypeEntry extends ProcedureEntry implements FisheryType {
    /**
     * Pour compatibilit�s entre les enregistrements binaires de diff�rentes versions.
     */
    private static final long serialVersionUID = 6718082896471037388L;

    /**
     * Cr�e une nouvelle entr�e du nom sp�cifi�.
     */
    public FisheryTypeEntry(final String name, final String remarks) {
        super(name, remarks);
    }
}
