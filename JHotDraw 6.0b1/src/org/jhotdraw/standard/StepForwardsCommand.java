/*
 * @(#)BringToFrontCommand.java
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
import org.jhotdraw.standard.StepBackwardsCommand.UndoActivity;
import org.jhotdraw.util.Undoable;

/**
 * BringToFrontCommand brings the selected figures in the front of
 * the other figures.
 *
 * @see SendToBackCommand
 * @version <$CURRENT_VERSION$>
 */
public class StepForwardsCommand extends AbstractCommand {

	/**
	 * Constructs a bring to front command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public StepForwardsCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		if (fe.hasNextFigure()) {
			Figure fi = fe.nextFigure();
			view().drawing().sendToLayer(fi, ((UndoActivity) getUndoActivity()).getCurrentLayer(fi) + 1);
		}
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new StepForwardsCommand.UndoActivity(view());
	}

	public static class UndoActivity extends StepBackwardsCommand.UndoActivity {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
		}

		protected void sendToCommand(Figure f) {			
			getDrawingView().drawing().sendToLayer(f, this.getCurrentLayer(f) + 1);
		}
	}
}
