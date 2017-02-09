package br.com.zurcs.executor.comun;

import br.com.zurcs.base.persistence.CrudService;
import br.com.zurcs.executor.persistencia.entidades.Modulo;

public interface ModuloManager extends CrudService<Modulo, Integer> {

	void excluir(Modulo selectedContato);

}
