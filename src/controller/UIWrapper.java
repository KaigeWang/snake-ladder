package controller;

import javax.swing.*;
import java.awt.*;

public class UIWrapper {
	private Component parentComponent;

	public UIWrapper(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	public int showOptionMessage(String message) {

		String[] options = { "OK", "Quit" };
		int x = JOptionPane.showOptionDialog(parentComponent, message, "Information", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		return x;

	}

	public void showInfoMessage(String message) {
		JOptionPane.showMessageDialog(parentComponent, message, "Information", JOptionPane.PLAIN_MESSAGE);
	}

	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public String promptForInput(String promptMessage) {
		return JOptionPane.showInputDialog(parentComponent, promptMessage);
	}

	public int showComfirmDialog(String message) {
		return JOptionPane.showConfirmDialog(parentComponent, message);
	}

}