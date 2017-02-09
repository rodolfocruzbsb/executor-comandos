package br.com.zurcs.executor.view.controller;

import java.util.List;
import java.util.Optional;

import br.com.zurcs.commons.util.validators.IsNullUtil;
import br.com.zurcs.executor.business.ComandoManagerBean;
import br.com.zurcs.executor.business.ExecutorManagerBean;
import br.com.zurcs.executor.business.ModuloManagerBean;
import br.com.zurcs.executor.comun.ComandoManager;
import br.com.zurcs.executor.comun.ExecutorManager;
import br.com.zurcs.executor.comun.ModuloManager;
import br.com.zurcs.executor.main.MainApp;
import br.com.zurcs.executor.persistencia.entidades.Comando;
import br.com.zurcs.executor.persistencia.entidades.Modulo;
import br.com.zurcs.executor.view.ResourceController;
import br.com.zurcs.executor.view.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class TarefasOverviewController {

	private MainApp mainApp;

	private ModuloManager moduloManager;

	private ComandoManager comandoManager;

	private ExecutorManager executorManager;

	@FXML
	private TableView<Comando> comandosTable;

	@FXML
	private TableView<Modulo> modulosTable;

	@FXML
	private TableColumn<Modulo, String> nomeColumn;

	private final ObservableList<Modulo> modulos = FXCollections.observableArrayList();

	@FXML
	private TableColumn<Comando, String> comandoColumn;

	@FXML
	private TableColumn<Comando, String> comandoNomeColumn;

	@FXML
	private TableColumn<Comando, String> acaoColumn;

	@FXML
	private Label nomeLabel;

	public TarefasOverviewController() {

		this.moduloManager = new ModuloManagerBean();

		this.comandoManager = new ComandoManagerBean();

		this.executorManager = new ExecutorManagerBean();

	}

	@FXML
	private void initialize() {

		this.carregarTela();

	}

	@FXML
	private void handleDeleteModulo() {

		final Modulo selectedContato = this.modulosTable.getSelectionModel().getSelectedItem();

		if (IsNullUtil.isNotNullOrEmpty(selectedContato)) {

			final Alert alert = new Alert(AlertType.CONFIRMATION);

			alert.setTitle("Confirmação");

			alert.setHeaderText("Você está prestes a excluir o registro.");

			alert.setContentText("Deseja continuar esta operação?");

			final Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == ButtonType.OK) {
				try {

					this.moduloManager.excluir(selectedContato);

					this.modulosTable.getItems().remove(selectedContato);

				} catch (Exception e) {
					AlertUtils.alert(null, null, e);
				}
			}

		} else {

			AlertUtils.alertNenhumItemSelecionado();
		}

	}

	@FXML
	private void handleNewModulo() {

		this.mainApp.showModuloCrud(new Modulo());
	}

	@FXML
	private void handleEditModulo() {

		final Modulo selectedModulo = this.modulosTable.getSelectionModel().getSelectedItem();

		if (IsNullUtil.isNotNullOrEmpty(selectedModulo)) {

			this.mainApp.showModuloCrud(selectedModulo);
		} else {

			AlertUtils.alertNenhumItemSelecionado();
		}
	}

	private void carregarTela() {

		this.modulos.addAll(this.moduloManager.buscarTodos("nome"));

		this.modulosTable.setItems(this.modulos);

		this.nomeColumn.setCellValueFactory(new PropertyValueFactory<Modulo, String>("nome"));

		this.modulosTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.listarAcoes(newValue));

		final Image imagePlay = new Image(ResourceController.getInputStream("imagens/play2_48x48.png"));

		ImageView imageView = new ImageView(imagePlay);

		imageView.setFitHeight(12);

		imageView.setFitWidth(12);

		final Callback<TableColumn<Comando, String>, TableCell<Comando, String>> cellFactory = new Callback<TableColumn<Comando, String>, TableCell<Comando, String>>() {

			@Override
			public TableCell<Comando, String> call(final TableColumn<Comando, String> param) {

				final TableCell<Comando, String> cell = new TableCell<Comando, String>() {

					final Button btnExcluir = new Button("", imageView);

					@Override
					public void updateItem(final String item, final boolean empty) {

						super.updateItem(item, empty);

						if (empty) {

							this.setGraphic(null);

							this.setText(null);

						} else {
							this.btnExcluir.setOnAction((final ActionEvent event) -> {

								final Optional<ButtonType> result = AlertUtils.alertaConfirmacao();

								if (result.get() == ButtonType.OK) {

									try {

										final Comando comando = this.getTableView().getItems().get(this.getIndex());

										executorManager.execute(comando);

										AlertUtils.alert("Sua solicitação foi encaminhada para uma nova janela do seu Terminal.", "Sucesso!", "Tarefa enviada para execução.", AlertType.INFORMATION);
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

		acaoColumn.setCellFactory(cellFactory);

	}

	private void listarAcoes(Modulo modulo) {

		if (IsNullUtil.isNotNullOrEmpty(modulo)) {

			this.nomeLabel.setText(modulo.getNome() + " - " + modulo.getDescricao());

			final List<Comando> comandos = this.comandoManager.buscarPorModulo(modulo);

			this.comandosTable.setItems(FXCollections.observableArrayList(comandos));

			this.comandoNomeColumn.setCellValueFactory(new PropertyValueFactory<Comando, String>("nome"));

			this.comandoColumn.setCellValueFactory(new PropertyValueFactory<Comando, String>("comando"));
		} else {
			this.nomeLabel.setText("");
		}
	}

	public void setMainApp(final MainApp mainApp) {

		this.mainApp = mainApp;

	}

}
