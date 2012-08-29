/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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

package org.constellation.swing;

import java.util.ResourceBundle;
import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class LayerRowModel implements RowModel {

    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org/constellation/swing/Bundle");
    
    public static class EditLayer{}
    public static class DeleteLayer{}
    
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueFor(Object o, int i) {
        //do nothing, edition are just actions
        return o;
    }

    @Override
    public Class getColumnClass(int i) {
        switch(i){
            case 0 : return EditLayer.class;
            case 1 : return DeleteLayer.class;
            default: return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return true;
    }

    @Override
    public void setValueFor(Object o, int i, Object o1) {
        //do nothing
    }

    @Override
    public String getColumnName(int i) {
        switch(i){
            case 0 : return BUNDLE.getString("edit");
            case 1 : return BUNDLE.getString("delete");
            default: return "";
        }
    }
    
}
