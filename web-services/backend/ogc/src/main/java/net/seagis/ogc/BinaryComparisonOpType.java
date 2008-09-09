/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package net.seagis.ogc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import net.seagis.coverage.web.ExpressionType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for BinaryComparisonOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryComparisonOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/ogc}expression"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}Literal"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="matchCase" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryComparisonOpType", propOrder = {
    "expressionOrLiteralOrPropertyName"
})
public class BinaryComparisonOpType extends ComparisonOpsType {

    @XmlElements({
        @XmlElement(name = "expression", type = ExpressionType.class, nillable = true),
        @XmlElement(name = "PropertyName", type = String.class, nillable = true),
        @XmlElement(name = "Literal", type = LiteralType.class, nillable = true)
    })
    private List<Object> expressionOrLiteralOrPropertyName;
    @XmlAttribute
    private Boolean matchCase;

    /**
     * Empty constructor used by JAXB
     */
    BinaryComparisonOpType() {
        
    }
    
    /**
     * Build a new Binary comparison operator
     */
    public BinaryComparisonOpType(LiteralType literal, PropertyNameType propertyName, Boolean matchCase) {
        expressionOrLiteralOrPropertyName = new ArrayList<Object>();
        expressionOrLiteralOrPropertyName.add(literal);
        if (propertyName != null)
        expressionOrLiteralOrPropertyName.add(propertyName.getPropertyName());
        this.matchCase = matchCase;
    }
    
    /**
     * Gets the value of the expressionOrLiteralOrPropertyName property.
     * (unmodifiable)
     */
    public List<Object> getExpressionOrLiteralOrPropertyName() {
        if (expressionOrLiteralOrPropertyName == null) {
            expressionOrLiteralOrPropertyName = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(expressionOrLiteralOrPropertyName);
    }

    /**
     * Gets the value of the matchCase property.
     */
    public boolean isMatchingCase() {
        return matchCase;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("MatchCase ? ").append(matchCase).append('\n');
        int i = 0;
        if (expressionOrLiteralOrPropertyName != null) {
            for (Object obj: expressionOrLiteralOrPropertyName) {
                s.append(i).append(": ").append("class: ").append(obj.getClass().getSimpleName());
                s.append('\n').append(obj.toString()).append('\n');
            }
        }
        
        return s.toString();
    }

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Expression getExpression1() {
        String propertyName = null;
        for (Object obj : getExpressionOrLiteralOrPropertyName()) {
            if (obj instanceof String ) {
                propertyName = (String) obj;
            } else if (obj instanceof PropertyNameType) {
                return (PropertyNameType)obj;
            }
        }
        return new PropertyNameType(propertyName);
    }

    public Expression getExpression2() {
        LiteralType literal = null;
        for (Object obj : getExpressionOrLiteralOrPropertyName()) {
            if (obj instanceof LiteralType) {
                literal = (LiteralType) obj;
            } 
        }
        return literal;
    }
}
