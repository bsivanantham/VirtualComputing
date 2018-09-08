package com.mxgraph.examples.swing.editor.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AbstractActionWrapper extends AbstractAction {

	private Action action;
	private Object eventSource;

	public AbstractActionWrapper(Object es, String name, Action a, ImageIcon icon) {
		super(name,icon);
		System.out.println("AbstractActionWrapper: action"+ a+"object "+es+" string "+name+" image icon "+icon);
		action=a;
		eventSource=es;
	}

	public void setInternalAction(Action a) {
		action=a;
	}
	public Action getInternalAction() {
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("eventSource:   "+ e.getID()+e.getActionCommand());
		action.actionPerformed(new ActionEvent(eventSource, e.getID(), e.getActionCommand()));
	}

}
