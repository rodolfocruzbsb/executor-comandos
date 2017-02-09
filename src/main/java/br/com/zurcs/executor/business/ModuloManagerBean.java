package br.com.zurcs.executor.business;

import br.com.zurcs.base.core.entity.EntidadeUtil;
import br.com.zurcs.base.exceptions.ExceptionUtil;
import br.com.zurcs.commons.util.validators.IsNullUtil;
import br.com.zurcs.executor.business.crud.CrudServiceBean;
import br.com.zurcs.executor.comun.ComandoManager;
import br.com.zurcs.executor.comun.ModuloManager;
import br.com.zurcs.executor.persistencia.entidades.Modulo;

public class ModuloManagerBean extends CrudServiceBean<Modulo, Integer> implements ModuloManager {

	private ComandoManager comandoManager;

	public ModuloManagerBean() {

		this.comandoManager = new ComandoManagerBean();
	}

	@Override
	public void excluir(Modulo modulo) {

		ExceptionUtil.throwsIfTrue("XXXX", "Módulo não pode ser nulo ou inválido.", EntidadeUtil.isNullOrEmptyEntidade(modulo));

		if (IsNullUtil.isNotNullOrEmpty(modulo.getComandos())) {

			modulo.getComandos().forEach(m -> comandoManager.excluir(m.getId()));
		}

		this.excluir(modulo.getId());

	}

}
