/**
 * $Id: mxPerimeter.java,v 1.18 2009/12/28 12:12:54 gaudenz Exp $
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.view;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;

/**
 * Provides various perimeter functions to be used in a style
 * as the value of mxConstants.STYLE_PERIMETER. Alternatevly, the mxConstants.
 * PERIMETER_* constants can be used to reference a perimeter via the
 * mxStyleRegistry.
 */
public class mxPerimeter
{

	/**
	 * Defines the requirements for a perimeter function.
	 */
	public interface mxPerimeterFunction
	{

		/**
		 * Implements a perimeter function. The edgeState and
		 * terminalState refer to the states of the edge and terminal cells.
		 * The edge state may be null if the perimeter is used for inserting new
		 * edges. isSource is true if the given terminal is the source of the edge,
		 * and next refers to the nearest point on the edge. Note that you should
		 * not read the points from the edgeState at this point.
		 * 
		 * @param bounds Rectangle that represents the absolute bounds of the
		 * vertex.
		 * @param edgeState Cell state that represents the incoming or outgoing
		 * edge.
		 * @param terminalState Cell state that represents the vertex.
		 * @param isSource Boolean that specifies if the vertex is the source
		 * terminal.
		 * @param next Point that represents the nearest neighbour point on the
		 * given edge.
		 * @return Returns the perimeter point.
		 */
		mxPoint apply(mxRectangle bounds, mxCellState edgeState,
				mxCellState terminalState, boolean isSource, mxPoint next);

	}

	/**
	 * Describes a rectangular perimeter for the given bounds. 
	 */
	public static mxPerimeterFunction RectanglePerimeter = new mxPerimeterFunction()
	{

		/* (non-Javadoc)
		 * @see com.mxgraph.view.mxPerimeter.mxPerimeterFunction#apply(com.mxgraph.utils.mxRectangle, com.mxgraph.view.mxCellState, com.mxgraph.view.mxCellState, boolean, com.mxgraph.utils.mxPoint)
		 */
		public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
				mxCellState terminalState, boolean isSource, mxPoint next)
		{
			double cx = bounds.getCenterX();
			double cy = bounds.getCenterY();
			double dx = next.getX() - cx;
			double dy = next.getY() - cy;
			double alpha = Math.atan2(dy, dx);

			mxPoint p = new mxPoint();
			double pi = Math.PI;
			double pi2 = Math.PI / 2;
			double beta = pi2 - alpha;
			double t = Math.atan2(bounds.getHeight(), bounds.getWidth());

			if (alpha < -pi + t || alpha > pi - t)
			{
				// Left edge
				p.setX(bounds.getX());
				p.setY(cy - bounds.getWidth() * Math.tan(alpha) / 2);
			}
			else if (alpha < -t)
			{
				// Top Edge
				p.setY(bounds.getY());
				p.setX(cx - bounds.getHeight() * Math.tan(beta) / 2);
			}
			else if (alpha < t)
			{
				// Right Edge
				p.setX(bounds.getX() + bounds.getWidth());
				p.setY(cy + bounds.getWidth() * Math.tan(alpha) / 2);
			}
			else
			{
				// Bottom Edge
				p.setY(bounds.getY() + bounds.getHeight());
				p.setX(cx + bounds.getHeight() * Math.tan(beta) / 2);
			}

			if (edgeState != null
					&& edgeState.view.graph.isOrthogonal(edgeState,
							terminalState))
			{
				if (next.getX() >= bounds.getX()
						&& next.getX() <= bounds.getX() + bounds.getWidth())
				{
					p.setX(next.getX());
				}
				else if (next.getY() >= bounds.getY()
						&& next.getY() <= bounds.getY() + bounds.getHeight())
				{
					p.setY(next.getY());
				}

				if (next.getX() < bounds.getX())
				{
					p.setX(bounds.getX());
				}
				else if (next.getX() > bounds.getX() + bounds.getWidth())
				{
					p.setX(bounds.getX() + bounds.getWidth());
				}

				if (next.getY() < bounds.getY())
				{
					p.setY(bounds.getY());
				}
				else if (next.getY() > bounds.getY() + bounds.getHeight())
				{
					p.setY(bounds.getY() + bounds.getHeight());
				}
			}

			return p;
		}

	};

	/**
	 * Describes an elliptic perimeter.
	 */
	public static mxPerimeterFunction EllipsePerimeter = new mxPerimeterFunction()
	{

		/* (non-Javadoc)
		 * @see com.mxgraph.view.mxPerimeter.mxPerimeterFunction#apply(com.mxgraph.utils.mxRectangle, com.mxgraph.view.mxCellState, com.mxgraph.view.mxCellState, boolean, com.mxgraph.utils.mxPoint)
		 */
		public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
				mxCellState terminalState, boolean isSource, mxPoint next)
		{
			double x = bounds.getX();
			double y = bounds.getY();
			double a = bounds.getWidth() / 2;
			double b = bounds.getHeight() / 2;
			double cx = x + a;
			double cy = y + b;
			double px = next.getX();
			double py = next.getY();

			// Calculates straight line equation through
			// point and ellipse center y = d * x + h
			double dx = px - cx;
			double dy = py - cy;

			if (dx == 0)
			{
				return new mxPoint(cx, cy + b * dy / Math.abs(dy));
			}
			
			boolean orthogonal = edgeState != null
					&& edgeState.view.graph.isOrthogonal(edgeState,
							terminalState);

			if (orthogonal)
			{
				if (py >= y && py <= y + bounds.getHeight())
				{
					double ty = py - cy;
					double tx = Math.sqrt(a * a * (1 - (ty * ty) / (b * b)));

					if (Double.isNaN(tx))
					{
						tx = 0;
					}

					if (px <= x)
					{
						tx = -tx;
					}

					return new mxPoint(cx + tx, py);
				}

				if (px >= x && px <= x + bounds.getWidth())
				{
					double tx = px - cx;
					double ty = Math.sqrt(b * b * (1 - (tx * tx) / (a * a)));

					if (Double.isNaN(ty))
					{
						ty = 0;
					}

					if (py <= y)
					{
						ty = -ty;
					}

					return new mxPoint(px, cy + ty);
				}
			}

			// Calculates intersection
			double d = dy / dx;
			double h = cy - d * cx;
			double e = a * a * d * d + b * b;
			double f = -2 * cx * e;
			double g = a * a * d * d * cx * cx + b * b * cx * cx - a * a * b
					* b;
			double det = Math.sqrt(f * f - 4 * e * g);

			// Two solutions (perimeter points)
			double xout1 = (-f + det) / (2 * e);
			double xout2 = (-f - det) / (2 * e);
			double yout1 = d * xout1 + h;
			double yout2 = d * xout2 + h;
			double dist1 = Math.sqrt(Math.pow((xout1 - px), 2)
					+ Math.pow((yout1 - py), 2));
			double dist2 = Math.sqrt(Math.pow((xout2 - px), 2)
					+ Math.pow((yout2 - py), 2));

			// Correct solution
			double xout = 0;
			double yout = 0;

			if (dist1 < dist2)
			{
				xout = xout1;
				yout = yout1;
			}
			else
			{
				xout = xout2;
				yout = yout2;
			}

			return new mxPoint(xout, yout);
		}

	};

	/**
	 * Describes a rhombus (aka diamond) perimeter.
	 */
	public static mxPerimeterFunction RhombusPerimeter = new mxPerimeterFunction()
	{

		/* (non-Javadoc)
		 * @see com.mxgraph.view.mxPerimeter.mxPerimeterFunction#apply(com.mxgraph.utils.mxRectangle, com.mxgraph.view.mxCellState, com.mxgraph.view.mxCellState, boolean, com.mxgraph.utils.mxPoint)
		 */
		public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
				mxCellState terminalState, boolean isSource, mxPoint next)
		{
			double x = bounds.getX();
			double y = bounds.getY();
			double w = bounds.getWidth();
			double h = bounds.getHeight();

			double cx = x + w / 2;
			double cy = y + h / 2;

			double px = next.getX();
			double py = next.getY();

			// Special case for intersecting the diamond's corners
			if (cx == px)
			{
				if (cy > py)
				{
					return new mxPoint(cx, y); // top
				}
				else
				{
					return new mxPoint(cx, y + h); // bottom
				}
			}
			else if (cy == py)
			{
				if (cx > px)
				{
					return new mxPoint(x, cy); // left
				}
				else
				{
					return new mxPoint(x + w, cy); // right
				}
			}

			double tx = cx;
			double ty = cy;

			if (edgeState != null
					&& edgeState.view.graph.isOrthogonal(edgeState,
							terminalState))
			{
				if (px >= x && px <= x + w)
				{
					tx = px;
				}
				else if (py >= y && py <= y + h)
				{
					ty = py;
				}
			}

			// In which quadrant will the intersection be?
			// set the slope and offset of the border line accordingly
			if (px < cx)
			{
				if (py < cy)
				{
					return mxUtils.intersection(px, py, tx, ty, cx, y, x, cy);
				}
				else
				{
					return mxUtils.intersection(px, py, tx, ty, cx, y + h, x,
							cy);
				}
			}
			else if (py < cy)
			{
				return mxUtils.intersection(px, py, tx, ty, cx, y, x + w, cy);
			}
			else
			{
				return mxUtils.intersection(px, py, tx, ty, cx, y + h, x + w,
						cy);
			}
		}

	};

	/**
	 * Describes a triangle perimeter. See RectanglePerimeter
	 * for a description of the parameters.
	 */
	public static mxPerimeterFunction TrianglePerimeter = new mxPerimeterFunction()
	{

		/* (non-Javadoc)
		 * @see com.mxgraph.view.mxPerimeter.mxPerimeterFunction#apply(com.mxgraph.utils.mxRectangle, com.mxgraph.view.mxCellState, com.mxgraph.view.mxCellState, boolean, com.mxgraph.utils.mxPoint)
		 */
		public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
				mxCellState terminalState, boolean isSource, mxPoint next)
		{
			boolean orthogonal = edgeState != null
					&& edgeState.view.graph.isOrthogonal(edgeState,
							terminalState);

			Object direction = (terminalState != null) ? mxUtils.getString(
					terminalState.style, mxConstants.STYLE_DIRECTION,
					mxConstants.DIRECTION_EAST) : mxConstants.DIRECTION_EAST;
			boolean vertical = direction.equals(mxConstants.DIRECTION_NORTH)
					|| direction.equals(mxConstants.DIRECTION_SOUTH);

			double x = bounds.getX();
			double y = bounds.getY();
			double w = bounds.getWidth();
			double h = bounds.getHeight();

			double cx = x + w / 2;
			double cy = y + h / 2;

			mxPoint start = new mxPoint(x, y);
			mxPoint corner = new mxPoint(x + w, cy);
			mxPoint end = new mxPoint(x, y + h);

			if (direction.equals(mxConstants.DIRECTION_NORTH))
			{
				start = end;
				corner = new mxPoint(cx, y);
				end = new mxPoint(x + w, y + h);
			}
			else if (direction.equals(mxConstants.DIRECTION_SOUTH))
			{
				corner = new mxPoint(cx, y + h);
				end = new mxPoint(x + w, y);
			}
			else if (direction.equals(mxConstants.DIRECTION_WEST))
			{
				start = new mxPoint(x + w, y);
				corner = new mxPoint(x, cy);
				end = new mxPoint(x + w, y + h);
			}

			// Compute angle
			double dx = next.getX() - cx;
			double dy = next.getY() - cy;

			double alpha = (vertical) ? Math.atan2(dx, dy) : Math.atan2(dy, dx);
			double t = (vertical) ? Math.atan2(w, h) : Math.atan2(h, w);

			boolean base = false;

			if (direction.equals(mxConstants.DIRECTION_NORTH)
					|| direction.equals(mxConstants.DIRECTION_WEST))
			{
				base = alpha > -t && alpha < t;
			}
			else
			{
				base = alpha < -Math.PI + t || alpha > Math.PI - t;
			}

			mxPoint result = null;

			if (base)
			{
				if (orthogonal
						&& ((vertical && next.getX() >= start.getX() && next
								.getX() <= end.getX()) || (!vertical
								&& next.getY() >= start.getY() && next.getY() <= end
								.getY())))
				{
					if (vertical)
					{
						result = new mxPoint(next.getX(), start.getY());
					}
					else
					{
						result = new mxPoint(start.getX(), next.getY());
					}
				}
				else
				{
					if (direction.equals(mxConstants.DIRECTION_EAST))
					{
						result = new mxPoint(x, y + h / 2 - w * Math.tan(alpha)
								/ 2);
					}
					else if (direction.equals(mxConstants.DIRECTION_NORTH))
					{
						result = new mxPoint(x + w / 2 + h * Math.tan(alpha)
								/ 2, y + h);
					}
					else if (direction.equals(mxConstants.DIRECTION_SOUTH))
					{
						result = new mxPoint(x + w / 2 - h * Math.tan(alpha)
								/ 2, y);
					}
					else if (direction.equals(mxConstants.DIRECTION_WEST))
					{
						result = new mxPoint(x + w, y + h / 2 + w
								* Math.tan(alpha) / 2);
					}
				}
			}
			else
			{
				if (orthogonal)
				{
					mxPoint pt = new mxPoint(cx, cy);

					if (next.getY() >= y && next.getY() <= y + h)
					{
						pt.setX((vertical) ? cx : ((direction
								.equals(mxConstants.DIRECTION_WEST)) ? x + w
								: x));
						pt.setY(next.getY());
					}
					else if (next.getX() >= x && next.getX() <= x + w)
					{
						pt.setX(next.getX());
						pt.setY((!vertical) ? cy : ((direction
								.equals(mxConstants.DIRECTION_NORTH)) ? y + h
								: y));
					}

					// Compute angle
					dx = next.getX() - pt.getX();
					dy = next.getY() - pt.getY();

					cx = pt.getX();
					cy = pt.getY();
				}

				if ((vertical && next.getX() <= x + w / 2)
						|| (!vertical && next.getY() <= y + h / 2))
				{
					result = mxUtils.intersection(next.getX(), next.getY(), cx,
							cy, start.getX(), start.getY(), corner.getX(),
							corner.getY());
				}
				else
				{
					result = mxUtils.intersection(next.getX(), next.getY(), cx,
							cy, corner.getX(), corner.getY(), end.getX(), end
									.getY());
				}
			}

			if (result == null)
			{
				result = new mxPoint(cx, cy);
			}

			return result;
		}

	};

}
