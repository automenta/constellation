/*
 * Sicade - Syst�mes int�gr�s de connaissances pour l'aide � la d�cision en environnement
 * (C) 2005, Institut de Recherche pour le D�veloppement
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
package net.sicade.observation;


/**
 * Classe de base des exceptions pouvant survenir lors d'une requ�te sur une base de donn�es.
 *
 * @version $Id$
 * @author Remi Eve
 * @author Martin Desruisseaux
 */
public class CatalogException extends Exception {
    /**
     * Pour compatibilit�s entre les enregistrements binaires de diff�rentes versions.
     */
    private static final long serialVersionUID = 3838293108990270182L;

    /**
     * Nom de la table dans laquelle l'enregistrement �tait attendu, ou {@code null} si inconnu.
     */
    private String table;

    /**
     * Construit une exception avec le message sp�cifi�.
     */
    public CatalogException(final String message) {
        super(message);
    }

    /**
     * Construit une exception avec le message et le nom de table sp�cifi�.
     *
     * @param message Message d�crivant l'erreur.
     * @param table Nom de la table dans laquelle un probl�me est survenu, ou {@code null} si inconnu.
     */
    public CatalogException(final String message, final String table) {
        super(message);
        this.table = table;
    }

    /** 
     * Construit une exception avec la cause sp�cifi�e.
     * Le message sera d�termin�e � partir de la cause.
     */
    public CatalogException(final Exception cause) {
        super(cause.getLocalizedMessage(), cause);
    }

    /** 
     * Construit une exception avec la cause sp�cifi�e.
     * Le message sera d�termin�e � partir de la cause.
     */
    CatalogException(final Exception cause, final String table) {
        this(cause);
        this.table = table;
    }

    /**
     * Retourne le nom de la table dans laquelle un probl�me est survenu.
     * Peut retourner {@code null} si le nom de la table n'est pas connu.
     */
    public String getTable() {
        if (table != null) {
            table = table.trim();
            if (table.length() == 0) {
                table = null;
            }
        }
        return table;
    }
}
