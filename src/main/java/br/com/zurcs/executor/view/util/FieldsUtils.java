package br.com.zurcs.executor.view.util;

import static javafx.scene.input.KeyCode.F1;
import static javafx.scene.input.KeyCode.F10;
import static javafx.scene.input.KeyCode.F11;
import static javafx.scene.input.KeyCode.F12;
import static javafx.scene.input.KeyCode.F2;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.F4;
import static javafx.scene.input.KeyCode.F5;
import static javafx.scene.input.KeyCode.F6;
import static javafx.scene.input.KeyCode.F7;
import static javafx.scene.input.KeyCode.F8;
import static javafx.scene.input.KeyCode.F9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FieldsUtils {

	private static List<KeyCode> ignoreKeyCodes;

	static {
		FieldsUtils.ignoreKeyCodes = new ArrayList<>();
		Collections.addAll(FieldsUtils.ignoreKeyCodes, new KeyCode[] { F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12 });
	}

	public static void ignoreKeys(final TextField textField) {

		textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(final KeyEvent keyEvent) {

				if (FieldsUtils.ignoreKeyCodes.contains(keyEvent.getCode())) {
					keyEvent.consume();
				}
			}
		});
	}

	public static void dateField(final TextField textField) {

		FieldsUtils.maxField(textField, 10);

		textField.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {

				if (newValue.intValue() < 11) {
					final String value = textField.getText()

							.replaceAll("[^0-9]", "")

							.replaceFirst("(\\d{2})(\\d)", "$1/$2")

							.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");

					Platform.runLater(() -> {

						textField.setText(value);
					});

					FieldsUtils.positionCaret(textField);
				}
			}
		});
	}

	public static void numericField(final TextField textField) {

		textField.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {

				if (newValue.intValue() > oldValue.intValue()) {
					final char ch = textField.getText().charAt(oldValue.intValue());
					if (!( ch >= '0' && ch <= '9' )) {
						textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
					}
				}
			}
		});
	}

	public static void monetaryField(final TextField textField) {

		textField.setAlignment(Pos.CENTER_RIGHT);
		textField.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {

				final String value = textField.getText()

						.replaceAll("[^0-9]", "")

						.replaceAll("([0-9]{1})([0-9]{14})$", "$1.$2")

						.replaceAll("([0-9]{1})([0-9]{11})$", "$1.$2")

						.replaceAll("([0-9]{1})([0-9]{8})$", "$1.$2")

						.replaceAll("([0-9]{1})([0-9]{5})$", "$1.$2")

						.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");

				Platform.runLater(() -> {

					textField.setText(value);
				});

				FieldsUtils.positionCaret(textField);

				textField.textProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {

						if (newValue.length() > 17) {
							textField.setText(oldValue);
						}
					}
				});
			}
		});

		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean aBoolean, final Boolean fieldChange) {

				if (!fieldChange) {
					final int length = textField.getText().length();
					if (length > 0 && length < 3) {
						textField.setText(textField.getText() + "00");
					}
				}
			}
		});
	}

	public static void cpfCnpjField(final TextField textField) {

		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean aBoolean, final Boolean fieldChange) {

				String value = textField.getText();
				if (!fieldChange) {
					if (textField.getText().length() == 11) {
						value = value.replaceAll("[^0-9]", "");
						value = value.replaceFirst("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})$", "$1.$2.$3-$4");
					}
					if (textField.getText().length() == 14) {
						value = value.replaceAll("[^0-9]", "");
						value = value.replaceFirst("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})$", "$1.$2.$3/$4-$5");
					}
				}
				textField.setText(value);
				if (textField.getText() != value) {
					textField.setText("");
					textField.insertText(0, value);
				}

			}
		});

		FieldsUtils.maxField(textField, 18);
	}

	public static void cnpjField(final TextField textField) {

		FieldsUtils.maxField(textField, 18);

		textField.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(final ObservableValue<? extends Number> observableValue, final Number number, final Number number2) {

				final String value = textField.getText()

						.replaceAll("[^0-9]", "")

						.replaceFirst("(\\d{2})(\\d)", "$1.$2")

						.replaceFirst("(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3")

						.replaceFirst("\\.(\\d{3})(\\d)", ".$1/$2")

						.replaceFirst("(\\d{4})(\\d)", "$1-$2");

				Platform.runLater(() -> {

					textField.setText(value);
				});
				FieldsUtils.positionCaret(textField);
			}
		});

	}

	private static void positionCaret(final TextField textField) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				// Posiciona o cursor sempre a direita.
				textField.positionCaret(textField.getText().length());
			}
		});
	}

	public static void maxField(final TextField textField, final Integer length) {

		textField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {

				if (newValue != null && newValue.length() > length) {
					textField.setText(oldValue);
				}
			}
		});
	}
}
