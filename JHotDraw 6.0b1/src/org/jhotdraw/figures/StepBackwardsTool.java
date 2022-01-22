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
import org.jhotdraw.util.Command;
import org.jhotdraw.util.Undoable;
import org.jhotdraw.util.UndoableCommand;

import java.awt.event.MouseEvent;

/**
 * Tool to step backwards a figure
 *
 * @version <$CURRENT_VERSION$>
 */
public class StepBackwardsTool extends AbstractTool {

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Command cmd;
	FigureSelectionListener fsl;

	public StepBackwardsTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
		this.cmd = new UndoableCommand(new StepBackwardsCommand("Step Backwards", newDrawingEditor));
		this.setEnabled(false);
	}
	
	/**
	 * Fired when the selected view changes.
	 * Subclasses should always call super.  ViewSelectionChanged() this allows
	 * the tools state to be updated and referenced to the new view.
	 */
	protected void viewSelectionChanged(DrawingView oldView, DrawingView newView) {
		super.viewSelectionChanged(oldView, newView);
		oldView.removeFigureSelectionListener(this.fsl);
		newView.addFigureSelectionListener(createFigureSelectionChangeListener());
		
	}
	

	private FigureSelectionListener createFigureSelectionChangeListener() {
		this.fsl = new FigureSelectionListener() {
			public void figureSelectionChanged(DrawingView view) {
				StepBackwardsTool.this.figureSelectionChanged(view);
			}			
		};
		
		return this.fsl;
	}


	protected void figureSelectionChanged(DrawingView view) {
		int numberOfSelectedFigures = view.selectionCount();
		if(numberOfSelectedFigures > 0) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}
	
	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		editor().toolDone();
	}
	
	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		editor().toolDone();
	}
	
	/**
	 * Send when an existing view is about to be destroyed.
	 */
	protected void viewDestroying(DrawingView view) {
		super.viewDestroying(view);
		this.setEnabled(false);
	}
	
	/**
	 * Activates the tool for use on the given view. This method is called
	 * whenever the user switches to this tool. Use this method to
	 * reinitialize a tool.
	 * Since tools will be disabled unless it is useable, there will always
	 * be an active view when this is called. based on isUsable()
	 * Tool should never be activated if the view is null.
	 * Ideally, the dditor should take care of that.
	 */
	public void activate() {
		cmd.execute();
		editor().toolDone();
		this.deactivate();
		this.setEnabled(false);
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
