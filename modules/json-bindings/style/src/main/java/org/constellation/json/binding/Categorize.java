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

package org.constellation.json.binding;


import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.constellation.json.util.StyleFactories.SF;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.expression.Expression;

/**
 * @author Benjamin Garcia (Geomatys)
 *
 */
public class Categorize implements Function{

	private static final long serialVersionUID = 1L;

	private List<InterpolationPoint> points = new ArrayList<InterpolationPoint>();

	private Double interval;
	
	private String nanColor;
	
	public Categorize() {
	}
	
	@SuppressWarnings("rawtypes")
	public Categorize(final org.geotoolkit.style.function.Categorize categorize){
		ensureNonNull("categorize", categorize);
        final Map<Expression,Expression> thresholdsMap = categorize.getThresholds();
        if(thresholdsMap!=null){
            for (final Map.Entry<Expression,Expression> entry : thresholdsMap.entrySet()) {
                final InterpolationPoint ip = new InterpolationPoint();
                final Expression expression = entry.getKey();
                final Expression colorHexExp = entry.getValue();

                if(colorHexExp instanceof DefaultLiteral){
                    final Object colorHex = ((DefaultLiteral)colorHexExp).getValue();
                    ip.setColor("#"+Integer.toHexString(((Color)colorHex).getRGB()).substring(2));
                }

                if(expression instanceof DefaultLiteral){
                    final Object obj = ((DefaultLiteral)expression).getValue();
                    if(obj instanceof Double){
                        if(Double.isNaN((double)obj)){
                            ip.setData(null);
                            nanColor = ip.getColor();
                        }else {
                            ip.setData((Number)obj);
                        }
                    }else if(StyleConstants.CATEGORIZE_LESS_INFINITY.equals(expression)){
                        continue; //skip for infinity first key it will be restored later.
                    }
                }
                points.add(ip);
            }
            this.interval = (double)categorize.getThresholds().size();
        }
	}


	public List<InterpolationPoint> getPoints() {
		return points;
	}

	public void setPoints(List<InterpolationPoint> points) {
		this.points = points;
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(Double interval) {
		this.interval = interval;
	}

    public String getNanColor() {
		return nanColor;
	}

	public void setNanColor(String nanColor) {
		this.nanColor = nanColor;
	}

	public org.opengis.filter.expression.Function toType() {

        //remove nan point if exists because it is added later, and it cause error for max/min values
        final List<InterpolationPoint> nullPoints = new ArrayList<>();
        for (final InterpolationPoint ip : points) {
            if(ip.getData() == null){
                nullPoints.add(ip);
            }
        }
        points.removeAll(nullPoints);

		// create first threshold map to create first categorize function.
		Map<Expression, Expression> values = new HashMap<>(0);
		values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, new DefaultLiteral<Color>(Color.GRAY));
		for (final InterpolationPoint ip : points) {
			final Color c = Color.decode(ip.getColor());
			values.put(new DefaultLiteral<Double>(ip.getData().doubleValue()), new DefaultLiteral<Color>(c));
		}
		final org.geotoolkit.style.function.Categorize categorize = SF.categorizeFunction(StyleConstants.DEFAULT_CATEGORIZE_LOOKUP, values, ThreshholdsBelongTo.PRECEDING, StyleConstants.DEFAULT_FALLBACK);

        // Iteration to find min and max values
		Double min = null, max= null;
        for (final InterpolationPoint ip : points) {
			if(min==null && max==null){
				min = ip.getData().doubleValue();
				max = ip.getData().doubleValue();
			}
			min = Math.min(min,ip.getData().doubleValue());
			max = Math.max(max,ip.getData().doubleValue());
		}
        
        //init final threshold map and coefficient
		final Map<Expression, Expression> valuesRecompute = new HashMap<>();
        if(nanColor !=null){
            valuesRecompute.put(new DefaultLiteral<Double>(Double.NaN), new DefaultLiteral<Color>(Color.decode(nanColor)));
        }

        if(min != null && max != null){
            valuesRecompute.put(StyleConstants.CATEGORIZE_LESS_INFINITY, new DefaultLiteral<Color>(categorize.evaluate(min, Color.class)));
            double coefficient = max-min;
            if(interval != null){
                if(coefficient!=1){
                    coefficient = coefficient/(interval-1);
                }
                // Loop to create values with new point evaluation
                for (int i = 0; i < interval; i++) {
                    double val = min + (coefficient * i);
                    Color color = categorize.evaluate(val, Color.class);
                    valuesRecompute.put(new DefaultLiteral<Double>(val), new DefaultLiteral<Color>(color));
                }
            }
        }

		return SF.categorizeFunction(StyleConstants.DEFAULT_CATEGORIZE_LOOKUP, valuesRecompute, ThreshholdsBelongTo.PRECEDING, StyleConstants.DEFAULT_FALLBACK);
		
	}
}
