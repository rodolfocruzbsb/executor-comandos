package br.com.zurcs.executor.comun;

import java.util.List;

import br.com.zurcs.base.persistence.CrudService;
import br.com.zurcs.executor.persistencia.entidades.Comando;
import br.com.zurcs.executor.persistencia.entidades.Modulo;

public interface ComandoManager extends CrudService<Comando, Integer> {

	List<Comando> buscarPorModulo(Modulo modulo);

}
