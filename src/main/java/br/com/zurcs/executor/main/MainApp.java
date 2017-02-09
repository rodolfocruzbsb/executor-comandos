package br.com.zurcs.executor.main;

import java.io.IOException;

import br.com.zurcs.executor.business.crud.FabricaDeEntityManager;
import br.com.zurcs.executor.business.flyway.FlywayIntegrator;
import br.com.zurcs.executor.persistencia.entidades.Modulo;
import br.com.zurcs.executor.view.ResourceController;
import br.com.zurcs.executor.view.controller.ModuloCrudController;
import br.com.zurcs.executor.view.controller.RootLayoutController;
import br.com.zurcs.executor.view.controller.TarefasOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;

	private BorderPane rootLayout;

	public MainApp() {

	}

	@Override
	public void start(final Stage primaryStage) {

		this.primaryStage = primaryStage;

		this.primaryStage.setTitle("Executor de Comandos");

		this.primaryStage.getIcons().add(new Image(ResourceController.getInputStream("imagens/bash.png")));

		this.initRootLayout();

		this.showTarefasOverview();
	}

	public void initRootLayout() {

		try {

			final FXMLLoader loader = new FXMLLoader();

			loader.setLocation(ResourceController.getUrl("view/RootLayout.fxml"));

			this.rootLayout = (BorderPane) loader.load();

			final Scene scene = new Scene(this.rootLayout);

			this.primaryStage.setScene(scene);

			final RootLayoutController controller = loader.getController();

			controller.setMainApp(this);

			this.primaryStage.show();

		} catch (final IOException e) {

			e.printStackTrace();
		}

	}

	public void showTarefasOverview() {

		try {

			final FXMLLoader loader = new FXMLLoader();

			loader.setLocation(ResourceController.getUrl("view/TarefasOverview.fxml"));

			final AnchorPane tarefasOverview = (AnchorPane) loader.load();

			this.rootLayout.setCenter(tarefasOverview);

			final TarefasOverviewController controller = loader.getController();

			controller.setMainApp(this);

		} catch (final IOException e) {

			e.printStackTrace();
		}
	}

	public void showModuloCrud(final Modulo modulo) {

		try {

			final FXMLLoader loader = new FXMLLoader();

			loader.setLocation(ResourceController.getUrl("view/ModuloCrud.fxml"));

			final AnchorPane contatoCrud = (AnchorPane) loader.load();

			this.rootLayout.setCenter(contatoCrud);

			final ModuloCrudController controller = loader.getController();

			controller.setModulo(modulo);

			controller.setMainApp(this);

			controller.loadTela();

		} catch (final IOException e) {

			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {

		return this.primaryStage;
	}

	public static void main(final String[] args) {

		MainApp.configuraDiretoriosDaAplicacao();

		MainApp.inicializaBancoDeDados();

		MainApp.printandoMensagemConsole();

		Application.launch(args);
	}

	private static void inicializaBancoDeDados() {

		FabricaDeEntityManager.getEntityManager();

		FlywayIntegrator.integrate();

	}

	private static void printandoMensagemConsole() {

		InitHelper.printInit();

	}

	private static void configuraDiretoriosDaAplicacao() {

		System.setProperty("current_dir", System.getProperty("user.dir"));
	}
}
