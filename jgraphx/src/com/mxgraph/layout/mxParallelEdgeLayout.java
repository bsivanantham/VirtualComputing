package com.mxgraph.layout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxCellPath;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

public class mxParallelEdgeLayout extends mxGraphLayout
{

	/**
	 * Specifies the spacing between the edges. Default is 20.
	 */
	protected int spacing;

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public mxParallelEdgeLayout(mxGraph graph)
	{
		this(graph, 20);
	}

	/**
	 * Constructs a new stack layout layout for the specified graph,
	 * spacing, orientation and offset.
	 */
	public mxParallelEdgeLayout(mxGraph graph, int spacing)
	{
		super(graph);
		this.spacing = spacing;
	}

	public void execute(Object parent) {
		execute(parent, -1);
	}
	/*
	 * (non-Javadoc)
	 * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
	 */
	public void execute(Object parent,int depth)
	{
		Map<String, List<Object>> lookup = findParallels(parent,depth);

		graph.getModel().beginUpdate();
		try
		{
			Iterator<List<Object>> it = lookup.values().iterator();

			while (it.hasNext())
			{
				List<Object> parallels = it.next();

				if (parallels.size() > 1)
				{
					layout(parallels);
				}
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}

	/**
	 * 
	 */
	protected Map<String, List<Object>> findParallels(Object parent,int depth) {
		Map<String, List<Object>> lookup = new Hashtable<String, List<Object>>();
		findParallels(parent,lookup,depth);
		return lookup;
	}
	protected Map<String, List<Object>> findParallels(Object parent,Map<String, List<Object>> lookup,int depth)
	{
		mxIGraphModel model = graph.getModel();
		int childCount = model.getChildCount(parent);

		for (int i = 0; i < childCount; i++)
		{
			mxCell child = (mxCell)model.getChildAt(parent, i);

			if (!isEdgeIgnored(child))
			{
				String id = getEdgeId(child);

				if (id != null)
				{
					if (!lookup.containsKey(id))
					{
						lookup.put(id, new ArrayList<Object>());
					}

					lookup.get(id).add(child);
				}
			} else if (model.isVertex(child)) {
				if (depth!=0) {
					findParallels(child,lookup,depth-1);
				}
			}
		}

		return lookup;
	}

	/**
	 * 
	 */
	protected String getEdgeId(Object edge)
	{
		mxGraphView view = graph.getView();
		Object src = view.getVisibleTerminal(edge, true);
		Object trg = view.getVisibleTerminal(edge, false);

		if (src instanceof mxICell && trg instanceof mxICell)
		{
			String srcId = mxCellPath.create((mxICell) src);
			String trgId = mxCellPath.create((mxICell) trg);

			return (srcId.compareTo(trgId) > 0) ? trgId + "-" + srcId : srcId
					+ "-" + trgId;
		}

		return null;
	}

	/**
	 * 
	 */
	protected void layout(List<Object> parallels)
	{
		Object edge = parallels.get(0);
		mxIGraphModel model = graph.getModel();
		
		mxGraphView view = graph.getView();
		double scale = view.getScale();
		mxPoint trans = view.getTranslate();

		Object edgeParent = model.getParent(edge);
		mxCellState edgeParentState=view.getState(edgeParent);
		
		mxCellState cellState=view.getState(edge);		
		int lastIndex=cellState.getAbsolutePointCount()-1;
		Point start = cellState.getAbsolutePoint(0).getPoint();
		Point end = cellState.getAbsolutePoint(lastIndex).getPoint();
		start=edgeParentState.relativizePointToThisState(start, scale, trans);
		end=edgeParentState.relativizePointToThisState(end, scale, trans);
		
		mxGeometry src = model.getGeometry(model.getTerminal(edge, true));
		mxGeometry trg = model.getGeometry(model.getTerminal(edge, false));
		
		// Routes multiple loops
		if (src == trg)
		{
			//in the current code loops (i.e. edges with src==trg) are the children of the cell of
			// which they are attached (i.e. src) so there is no need to sum src.getX and src.getY
			// because the coordinates of the edge are already relative to that point.
			//double x0 = src.getX() + src.getWidth() + this.spacing;
			//double y0 = src.getY() + src.getHeight() / 2;
			double x0 = src.getWidth() + this.spacing;
			double y0 = src.getHeight() / 2;

			for (int i = 0; i < parallels.size(); i++)
			{
				route(parallels.get(i), x0, y0);
				x0 += spacing;
			}
		}
		else if (src != null && trg != null)
		{
			// Routes parallel edges
			/*double scx = src.getX() + src.getWidth() / 2;
			double scy = src.getY() + src.getHeight() / 2;
			double tcx = trg.getX() + trg.getWidth() / 2;
			double tcy = trg.getY() + trg.getHeight() / 2;*/
			// changed to position of starting and end points instead of center of source and target states.
			double scx = start.getX();
			double scy = start.getY();
			double tcx = end.getX();
			double tcy = end.getY();

			double dx = tcx - scx;
			double dy = tcy - scy;

			double len = Math.sqrt(dx * dx + dy * dy);

			double x0 = scx + dx / 2;
			double y0 = scy + dy / 2;

			double nx = dy * spacing / len;
			double ny = dx * spacing / len;

			x0 += nx * (parallels.size() - 1) / 2;
			y0 -= ny * (parallels.size() - 1) / 2;

			for (int i = 0; i < parallels.size(); i++)
			{
				mxCell parallelEdge = (mxCell) parallels.get(i);
				/*mxCellState edgeState=graph.getView().getState(parallelEdge);
				int numPoints=edgeState.getAbsolutePointCount();
				mxPoint first=edgeState.getAbsolutePoint(0);
				mxPoint last=edgeState.getAbsolutePoint(numPoints-1);
				System.out.println(first+" "+last);*/
				route(parallelEdge, x0, y0);
				x0 -= nx;
				y0 += ny;
			}
		}
	}

	/**
	 * 
	 */
	protected void route(Object edge, double x, double y)
	{
		if (graph.isCellMovable(edge))
		{
			setEdgePoints(edge, Arrays.asList(new mxPoint[] { new mxPoint(x, y) }));
		}
	}

}
