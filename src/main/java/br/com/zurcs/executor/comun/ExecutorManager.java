package br.com.zurcs.executor.comun;

import br.com.zurcs.executor.persistencia.entidades.Comando;

public interface ExecutorManager {

	public void executeInTextArea(Comando comando);

	void execute(Comando comando);

}
