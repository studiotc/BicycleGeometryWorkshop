/*
 * The MIT License
 *
 * Copyright 2019 Tom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bicycleGeometryWorkshop.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


/**
 * Common Graphics utility functions.
 * @author Tom
 */
public class Graphics {
    


    /**
     * render a cross shaped handle.
     * @param g2  The graphics object to render to.
     * @param color The color of the handle.
     * @param point  Location of the handle.
     * @param scale Scale for line weight, etc.
     */
    public static void renderCrossHandle(Graphics2D g2, Color color, Point2D point, float scale) {

        float lw = 1.5f / scale;
        BasicStroke stroke = new BasicStroke(lw);

        g2.setColor(color);
        g2.setStroke(stroke);

        double x = point.getX();
        double y = point.getY();

        double length = 10 / scale;

        Line2D lineHoriz = new Line2D.Double(x - length, y, x + length, y);
        Line2D lineVert = new Line2D.Double(x, y - length, x, y + length);

        g2.draw(lineHoriz);
        g2.draw(lineVert);

    }    
    
    
    
}
