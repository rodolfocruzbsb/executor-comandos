package br.com.zurcs.executor.view.controller;

import br.com.zurcs.commons.util.hardware.HardwareUtils;
import br.com.zurcs.executor.main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class RootLayoutController {

	private MainApp mainApp;

	public void setMainApp(final MainApp mainApp) {

		this.mainApp = mainApp;
	}

	public MainApp getMainApp() {

		return this.mainApp;
	}

	@FXML
	private void handleAbout() {

		final Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle("Executor de Ambiente - Guideware");

		alert.setHeaderText("Sobre");

		alert.getDialogPane().setPrefWidth(500);
		StringBuilder msg = new StringBuilder(10);
		msg.append("S.O. Name: ").append(HardwareUtils.getNameOS()).append("\n");
		msg.append("S.O. Version: ").append(HardwareUtils.getOsVersion()).append("\n");
		msg.append("Author: Rodolfo Cruz - https://twitter.com/RodolfocruzTi");
		alert.setContentText(msg.toString());

		alert.showAndWait();
	}

	@FXML
	private void handleExit() {

		System.exit(0);
	}
}
