/*
 * @(#)SendToBackCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.standard;

import org.jhotdraw.framework.*;
import org.jhotdraw.util.UndoableAdapter;
import org.jhotdraw.util.Undoable;
import java.util.*;

/**
 * A command to send the selection to the back of the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */ 
public class StepBackwardsCommand extends AbstractCommand {

	/**
	 * Constructs a send to back command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public StepBackwardsCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selectionZOrdered());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		if (fe.hasNextFigure()) {
			Figure fi = fe.nextFigure();
			int layer = ((UndoActivity) getUndoActivity()).getCurrentLayer(fi);
			if(layer > 0) {
				view().drawing().sendToLayer(fi, layer - 1);
			}
		}
		view().checkDamage();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new StepBackwardsCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter { 
		private Hashtable myOriginalLayers;
		
		public UndoActivity(DrawingView newDrawingView) { 
			super(newDrawingView);
			myOriginalLayers = new Hashtable();
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			FigureEnumeration fe = getAffectedFigures();
			if (fe.hasNextFigure()) {
				Figure currentFigure = fe.nextFigure();
				int currentFigureLayer = getOriginalLayer(currentFigure);
				getDrawingView().drawing().sendToLayer(currentFigure, currentFigureLayer);
			}
			
			return true;
		}
		
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}
			
			FigureEnumeration fe = getAffectedFigures();
			if (fe.hasNextFigure()) {
				sendToCommand(fe.nextFigure());
			}
			
			return true;			
		}

		protected void sendToCommand(Figure f) {
			int currentFigureLayer = getOriginalLayer(f);
			getDrawingView().drawing().sendToLayer(f, currentFigureLayer-1);
		}
		
		protected void addOriginalLayer(Figure affectedFigure, int newOriginalLayer) {
			myOriginalLayers.put(affectedFigure, new Integer(newOriginalLayer));
		}
		
		protected int getOriginalLayer(Figure lookupAffectedFigure) {
			return ((Integer)myOriginalLayers.get(lookupAffectedFigure)).intValue();
		}

		public void setAffectedFigures(FigureEnumeration fe) {
			// first make copy of FigureEnumeration in superclass
			super.setAffectedFigures(fe);
			// then get new FigureEnumeration of copy to save attributes
			FigureEnumeration copyFe = getAffectedFigures();
			if (copyFe.hasNextFigure()) {
				Figure f = copyFe.nextFigure();
				int originalLayer = getDrawingView().drawing().getLayer(f);
				addOriginalLayer(f, originalLayer);
			}
		}
		
		public int getCurrentLayer(Figure f) {
			int currentFigureLayer = getOriginalLayer(f);
			return currentFigureLayer;
		}
	}
}
