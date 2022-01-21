/*
 * @(#)StarTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.figures;

import org.jhotdraw.framework.*;
import org.jhotdraw.standard.*;
import org.jhotdraw.util.Undoable;
import java.awt.event.MouseEvent;

/**
 * Tool to star a PolyLineFigure
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public class StarTool extends AbstractTool {

	private PolyLineFigure  fStar;
	private int             fLastX, fLastY;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	public StarTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		super.activate();
	}

	public void deactivate() {
		super.deactivate();
		if (fStar != null) {
			if (fStar.size().width < 4 || fStar.size().height < 4) {
				getActiveDrawing().remove(fStar);
				// nothing to undo
				setUndoActivity(null);
			}
			fStar = null;
		}
	}

	private void point(int x, int y) {
		if (fStar == null) {
			fStar = new PolyLineFigure(x, y);
			setAddedFigure(view().add(fStar));
		}
		else if (fLastX != x || fLastY != y) {
			fStar.addPoint(x, y);
		}

		fLastX = x;
		fLastY = y;
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		if (e.getClickCount() >= 2) {
			setUndoActivity(createUndoActivity());
			getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));
		}
		else {	// Algorithm for passing points		
			double alpha = (2 * Math.PI) / 10; 
			double radius = 100;			
			for(int i = 11; i != 0; i--)
			{
			    double r = radius*(i % 2 + 1)/2;
			    double omega = alpha * i;
			    point(new Double((r * Math.sin(omega)) + e.getX()).intValue(), new Double((r * Math.cos(omega)) + e.getY()).intValue());
			}
			this.deactivate();			
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		// deactivate tool only when mouseUp was also fired
		if (e.getClickCount() >= 2) {
			editor().toolDone();
		}
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	private void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
