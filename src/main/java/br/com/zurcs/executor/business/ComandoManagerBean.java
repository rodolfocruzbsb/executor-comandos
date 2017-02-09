package br.com.zurcs.executor.business;

import java.util.List;

import br.com.zurcs.base.util.QueryParameter;
import br.com.zurcs.executor.business.crud.CrudServiceBean;
import br.com.zurcs.executor.comun.ComandoManager;
import br.com.zurcs.executor.persistencia.entidades.Comando;
import br.com.zurcs.executor.persistencia.entidades.Modulo;

public class ComandoManagerBean extends CrudServiceBean<Comando, Integer> implements ComandoManager {

	@Override
	public List<Comando> buscarPorModulo(final Modulo modulo) {

		this.checkNullOrEmptyParameters(modulo);

		this.checkNullOrEmptyParameters(modulo.getId());

		return buscarPorQueryNomeada("br.com.zurcs.executor.business.ComandoManagerBean.buscarPorModulo", QueryParameter.with("idModulo", modulo.getId()));
	}

}
