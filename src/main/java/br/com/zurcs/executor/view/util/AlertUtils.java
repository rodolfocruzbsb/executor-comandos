package br.com.zurcs.executor.view.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import br.com.zurcs.commons.util.validators.IsNullUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AlertUtils {

	public static void alert(final Exception e) {

		AlertUtils.alert(null, null, e);
	}

	public static void alert(final String title, final String headerTitle, final Exception e) {

		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(IsNullUtil.isNullOrEmpty(title) ? "Erro" : title);
		alert.setHeaderText(IsNullUtil.isNullOrEmpty(headerTitle) ? "Ops! Algo inesperado aconteceu" : headerTitle);
		alert.setContentText(null);

		alert.getDialogPane().setPrefWidth(500);

		// Create expandable Exception.
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		final String exceptionText = sw.toString();

		final Label label = new Label("Stacktrace do erro:");

		final TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	public static Optional<ButtonType> alertaExclusao() {

		final Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle("Confirmação");

		alert.setHeaderText("Você está prestes a excluir o registro.");

		alert.setContentText("Deseja continuar esta operação?");

		final Optional<ButtonType> result = alert.showAndWait();

		return result;
	}

	public static Optional<ButtonType> alertaConfirmacao() {

		final Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle("Confirmação");

		alert.setHeaderText("Você está prestes a executar uma ação.");

		alert.setContentText("Deseja continuar esta operação?");

		final Optional<ButtonType> result = alert.showAndWait();

		return result;
	}

	public static void alertNenhumItemSelecionado() {

		final Alert alert = new Alert(AlertType.WARNING);

		alert.setTitle("Nenhum Item foi selecionado");

		alert.setHeaderText("Nenhum item foi selecionado");

		alert.setContentText("Por favor, selecione um item para realizar esta ação.");

		alert.setResizable(true);

		alert.getDialogPane().setPrefWidth(500);

		alert.showAndWait();
	}

	public static void alert(String msg, String title, String content, AlertType alertYType) {

		final Alert alert = new Alert(alertYType);

		alert.setTitle(title);

		alert.setHeaderText(content);

		alert.setContentText(msg);

		alert.setResizable(true);

		alert.getDialogPane().setPrefWidth(500);

		alert.showAndWait();
	}
}
