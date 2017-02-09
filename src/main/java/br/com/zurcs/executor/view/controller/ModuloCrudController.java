package br.com.zurcs.executor.view.controller;

import java.util.Optional;

import br.com.zurcs.base.exceptions.BusinessException;
import br.com.zurcs.base.exceptions.ExceptionUtil;
import br.com.zurcs.commons.util.validators.IsNullUtil;
import br.com.zurcs.executor.business.ModuloManagerBean;
import br.com.zurcs.executor.comun.ModuloManager;
import br.com.zurcs.executor.main.MainApp;
import br.com.zurcs.executor.persistencia.entidades.Comando;
import br.com.zurcs.executor.persistencia.entidades.Modulo;
import br.com.zurcs.executor.view.util.AlertUtils;
import br.com.zurcs.executor.view.util.FieldsUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ModuloCrudController {

	@FXML
	private TextField nomeField;

	@FXML
	private TextField descricaoField;

	@FXML
	private TableView<Comando> comandosTable;

	@FXML
	private TableColumn<Comando, String> comandoNomeColumn;

	@FXML
	private TableColumn<Comando, String> comandoColumn;

	@FXML
	private TextField comandoNomeField;

	@FXML
	private TextArea comandoTextArea;

	private MainApp mainApp;

	/** Atributo modulo. */
	private Modulo modulo;

	/** Atributo moduloManager. */
	private final ModuloManager moduloManager;

	public ModuloCrudController() {

		this.moduloManager = new ModuloManagerBean();
	}

	@FXML
	private void initialize() {

		FieldsUtils.maxField(this.nomeField, 100);

		FieldsUtils.maxField(this.descricaoField, 250);

		this.comandoNomeColumn.setCellValueFactory(new PropertyValueFactory<Comando, String>("nome"));

		this.comandoColumn.setCellValueFactory(new PropertyValueFactory<Comando, String>("comando"));

		final TableColumn<Comando, String> actionCol = new TableColumn<>("Ação");

		final Callback<TableColumn<Comando, String>, TableCell<Comando, String>> cellFactory = new Callback<TableColumn<Comando, String>, TableCell<Comando, String>>() {

			@Override
			public TableCell<Comando, String> call(final TableColumn<Comando, String> param) {

				final TableCell<Comando, String> cell = new TableCell<Comando, String>() {

					final Button btnExcluir = new Button("Excluir");

					@Override
					public void updateItem(final String item, final boolean empty) {

						super.updateItem(item, empty);

						if (empty) {

							this.setGraphic(null);

							this.setText(null);

						} else {
							this.btnExcluir.setOnAction((final ActionEvent event) -> {

								final Optional<ButtonType> result = AlertUtils.alertaExclusao();

								if (result.get() == ButtonType.OK) {

									try {

										this.getTableView().getItems().remove(this.getIndex());
									} catch (final Exception e) {

										AlertUtils.alert(e);
									}

								}

							});
							this.setGraphic(this.btnExcluir);
							this.setText(null);
						}
					}
				};
				return cell;
			}
		};

		actionCol.setCellFactory(cellFactory);

		this.comandosTable.getColumns().add(actionCol);

	}

	public void loadTela() {

		if (IsNullUtil.isNotNullOrEmpty(this.modulo)) {

			this.nomeField.setText(this.modulo.getNome());

			this.descricaoField.setText(this.modulo.getDescricao());

			if (IsNullUtil.isNotNull(this.modulo.getComandos())) {

				this.comandosTable.setItems(FXCollections.observableArrayList(this.modulo.getComandos()));

				this.comandosTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.carregarCamposParaAlterarComando(newValue));
			}

		}
	}

	private void carregarCamposParaAlterarComando(final Comando comando) {

		if (IsNullUtil.isNotNull(comando)) {

			this.comandoNomeField.setText(comando.getNome());

			this.comandoTextArea.setText(comando.getComando());
		}
	}

	public void setModulo(final Modulo modulo) {

		this.modulo = modulo;
	}

	public void setMainApp(final MainApp mainApp) {

		this.mainApp = mainApp;

	}

	@FXML
	private void handleSalvar() {

		try {

			this.modulo.setNome(this.nomeField.getText());

			this.modulo.setDescricao(this.descricaoField.getText());

			this.modulo.setComandos(this.comandosTable.getItems());

			this.moduloManager.salvar(this.modulo);

		} catch (final Exception e) {

			e.printStackTrace();

			AlertUtils.alert(e);

		}

	}

	@FXML
	private void handleCancelar() {

		this.mainApp.showTarefasOverview();
	}

	@FXML
	public void handleAdicionarComando() {

		try {

			ExceptionUtil.throwsIfTrue("XXXX", "Nome não foi preenchido.", IsNullUtil.isNullOrEmpty(this.comandoNomeField.getText()));

			ExceptionUtil.throwsIfTrue("XXXX", "Comando não foi preenchido.", IsNullUtil.isNullOrEmpty(this.comandoTextArea.getText()));

			final Comando item = this.comandosTable.getSelectionModel().getSelectedItem();

			ExceptionUtil.throwsIfTrue("XXXX", "Este nome já está sendo utilizado.", this.jaExisteModuloComMesmoNome(this.comandoNomeField.getText()));

			if (IsNullUtil.isNotNullOrEmpty(item)) {// ALTERANDO item

				item.setComando(this.comandoTextArea.getText());

				item.setNome(this.comandoNomeField.getText());

				this.comandosTable.getItems().set(this.comandosTable.getSelectionModel().getSelectedIndex(), item);

			} else {// NOVO item

				final Comando comandoToAdd = new Comando();

				comandoToAdd.setComando(this.comandoTextArea.getText());

				comandoToAdd.setModulo(this.modulo);

				comandoToAdd.setNome(this.comandoNomeField.getText());

				this.comandosTable.getItems().add(comandoToAdd);

			}

			this.limparCamposComando();

		} catch (final BusinessException e) {

			AlertUtils.alert(e.getMessage(), "Ops! Algo está errado", "Detalhamento:", AlertType.WARNING);
		} catch (final Exception e) {

			e.printStackTrace();

			AlertUtils.alert(e);
		}

	}

	private boolean jaExisteModuloComMesmoNome(final String nome) {

		final Comando comando = this.comandosTable.getSelectionModel().getSelectedItem();

		if (IsNullUtil.isNotNullOrEmpty(comando)) {

			return this.comandosTable.getItems().filtered(i -> i.getNome().equalsIgnoreCase(nome) && !i.equals(comando)).size() > 0;
		} else {

			return this.comandosTable.getItems().filtered(i -> i.getNome().equalsIgnoreCase(nome)).size() > 0;
		}

	}

	private void limparCamposComando() {

		this.comandoNomeField.clear();

		this.comandoTextArea.clear();
	}

	@FXML
	public void handleCancelarComando() {

		this.comandosTable.getSelectionModel().clearSelection();

		this.limparCamposComando();
	}

}
