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

public class ExecutorManagerBean implements ExecutorManager {

	/** Atributo logger. */
	protected Logger logger = LogManager.getLogger(getClass());

	@Override
	public void execute(Comando comando) {

		if (IsNullUtil.isNotNullOrEmpty(comando) && IsNullUtil.isNotNullOrEmpty(comando.getComando())) {

			ExecutorService executorService = Executors.newSingleThreadExecutor();

			executorService.submit(() -> {

				executeInNewThread(comando);

			});
			
			executorService.shutdown();

		}
	}

	private void executeInNewThread(Comando comando) {

		this.logger.info(">>>>>>>>>>>>>>>>>>>[COMMAND EXEC] Starting for \"" + comando.getNome() + "\"");
		this.logger.info("\n\n> " + comando.getComando());
		final Instant inicio = Instant.now();
		try {

			Process proc = Runtime.getRuntime().exec(comando.getComando());

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			// System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while (( s = stdInput.readLine() ) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			// System.out.println("Here is the standard error of the command (if any):\n");
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

}
