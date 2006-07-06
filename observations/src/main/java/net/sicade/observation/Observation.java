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

// Sicade
import net.sicade.observation.Element;


/**
 * Une observation effectu�e � une {@linkplain Station station}. Une m�me station peut contenir plusieurs
 * observations, � la condition que chaque observation porte sur un {@linkplain Observable observable}
 * diff�rent. Une observation n'est pas forc�ment une valeur num�rique. Il peut s'agir d'une observation
 * qualitative ou d'un vecteur par exemple. Lorsqu'une observation est quantifi�e par une valeur num�rique
 * scalaire, on utilisera le terme {@linkplain Measurement mesure}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 * @author Antoine Hnawia
 */
public interface Observation extends Element {
    /**
     * Retourne la station � laquelle a �t� effectu�e cette observation.
     */
    Station getStation();

    /**
     * Retourne le ph�nom�ne observ�.
     */
    Observable getObservable();
}
