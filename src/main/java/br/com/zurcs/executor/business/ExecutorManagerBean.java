package br.com.zurcs.executor.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.zurcs.base.exceptions.BusinessException;
import br.com.zurcs.commons.util.validators.IsNullUtil;
import br.com.zurcs.executor.comun.ExecutorManager;
import br.com.zurcs.executor.persistencia.entidades.Comando;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExecutorManagerBean implements ExecutorManager {

	/** Atributo logger. */
	protected Logger logger = LogManager.getLogger(getClass());

	@Override
	public void executeInTextArea(Comando comando) {
		
		final TextArea outputConsoleTextArea = showPopup();

		if (IsNullUtil.isNotNullOrEmpty(comando) && IsNullUtil.isNotNullOrEmpty(comando.getComando())) {

			ExecutorService executorService = Executors.newSingleThreadExecutor();

			executorService.submit(() -> {

				executeCmdInTextArea(comando, outputConsoleTextArea);

			});
			
			executorService.shutdown();

		}
	}
	
	@Override
	public void execute(Comando comando) {
		
		
		if (IsNullUtil.isNotNullOrEmpty(comando) && IsNullUtil.isNotNullOrEmpty(comando.getComando())) {
			
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			
			executorService.submit(() -> {
				
				executeCmd(comando);
				
			});
			
			executorService.shutdown();
			
		}
	}
	
	public TextArea showPopup() {

		final Stage newStage = new Stage();
		
		final VBox vbox = new VBox();
		
		final TextArea console = new TextArea();
		
		console.setPrefWidth(800);
		console.setPrefHeight(600);
		
		vbox.getChildren().add(console);

		Scene stageScene = new Scene(vbox, 800, 600);
		newStage.setScene(stageScene);
		newStage.show();
		
		return console;
	}

	private void executeCmd(Comando comando) {

		this.logger.info(">>>>>>>>>>>>>>>>>>>[COMMAND EXEC] Starting for \"" + comando.getNome() + "\"");
		this.logger.info("\n\n> " + comando.getComando());
		final Instant inicio = Instant.now();
		try {

			Process proc = Runtime.getRuntime().exec(comando.getComando());

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			String s = null;
			while (( s = stdInput.readLine() ) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			String s2;
			while (( s2 = stdError.readLine() ) != null) {
				System.out.println(s2);
			}
			System.out.println("\n\n\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("", "Erro na execução da tarefa", e);
		}
		final Duration duration = Duration.between(inicio, Instant.now());
		this.logger.info(">>>>>>>>>>>>>>>>>>>[COMMAND EXEC] Finished for \"" + comando.getNome() + "\" in " + duration.getSeconds() + " Seconds");

	}
	
	private void executeCmdInTextArea(Comando comando, TextArea outputConsoleTextArea) {
		
		outputConsoleTextArea.appendText(">>>>>>>>>>>>>>>>>>>[COMMAND EXEC] Starting for \"" + comando.getNome() + "\"\n");
		
		outputConsoleTextArea.appendText("> " + comando.getComando()+"\n");
		
		final Instant inicio = Instant.now();
		
		try {
			
			Process proc = Runtime.getRuntime().exec(comando.getComando());
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			// read the output from the command
			String s = null;
			while (( s = stdInput.readLine() ) != null) {
				outputConsoleTextArea.appendText(s+"\n");
			}
			
			// read any errors from the attempted command
			String s2;
			while (( s2 = stdError.readLine() ) != null) {
				outputConsoleTextArea.appendText(s2+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("", "Erro na execução da tarefa", e);
		}
		final Duration duration = Duration.between(inicio, Instant.now());
		outputConsoleTextArea.appendText(">>>>>>>>>>>>>>>>>>>[COMMAND EXEC] Finished for \"" + comando.getNome() + "\" in " + duration.getSeconds() + " Seconds");
		
	}

}
