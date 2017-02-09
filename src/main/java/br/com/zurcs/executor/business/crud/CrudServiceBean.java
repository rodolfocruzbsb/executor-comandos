package br.com.zurcs.executor.business.crud;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.internal.SessionImpl;

import br.com.zurcs.base.core.entity.EntidadeUtil;
import br.com.zurcs.base.entity.Entidade;
import br.com.zurcs.base.exceptions.BusinessException;
import br.com.zurcs.base.exceptions.EntityNotFoundException;
import br.com.zurcs.base.exceptions.ExceptionUtil;
import br.com.zurcs.base.exceptions.ParameterNotFoundException;
import br.com.zurcs.base.exceptions.SeverityException;
import br.com.zurcs.base.persistence.CrudService;
import br.com.zurcs.base.util.MatchMode;
import br.com.zurcs.base.util.QueryParameter;
import br.com.zurcs.commons.util.validators.IsNullUtil;

public abstract class CrudServiceBean<T extends Entidade<ID>, ID extends Serializable> implements CrudService<T, ID> {

	protected Logger logger = Logger.getLogger(this.getClass());

	protected EntityManager entityManager;

	private final Class<T> clazz;

	@SuppressWarnings("unchecked")
	public CrudServiceBean() {

		this.clazz = (Class<T>) ( (ParameterizedType) this.getClass().getGenericSuperclass() ).getActualTypeArguments()[0];

		this.entityManager = FabricaDeEntityManager.getEntityManager();

	}

	/**
	 * 
	 * <p>
	 * Verifica se foi informada uma Entidade nao nula para a operacao, sendo alteracao, inclusao ou exclusao
	 * </p>
	 */
	private void validaEntidadeNula(final Entidade<ID> entidade) {

		ExceptionUtil.throwsIfTrue("XXX", "Entidade não pode ser nula.", EntidadeUtil.isNullEntidade(entidade));
	}

	@Override
	public void validate(final T entidade) {

	}

	@Override
	public boolean jaExiste(final T entidade) {

		return false;
	}

	@Override
	public T salvar(final T entity) {

		this.prepareBeforeValidate(entity);

		this.validaEntidadeNula(entity);

		this.validate(entity);

		this.prepare(entity);

		try {

			this.entityManager.getTransaction().begin();

			if (EntidadeUtil.isNullOrEmptyEntidade(entity)) {

				this.logger.debug("Incluindo entidade: " + entity);

				this.entityManager.persist(entity);

			} else {

				this.logger.debug("Alterando entidade: " + entity);

				this.entityManager.merge(entity);
			}

			this.entityManager.getTransaction().commit();
		} catch (Exception e) {

			this.entityManager.getTransaction().rollback();

			throw e;

		}

		return entity;
	}

	protected void prepareBeforeValidate(final T entity) {

	}

	@Override
	public T selecionarPorId(final ID id) {

		this.checkNullOrEmptyParameters(id);

		this.logger.debug("Selecionando Entidade: [" + this.clazz.getName() + " - Id [" + id + "]");

		final T result = this.entityManager.find(this.clazz, id);

		ExceptionUtil.throwsIfTrue("XXX", "Entidade não pode ser nula.", EntidadeUtil.isNullEntidade(result));

		return result;
	}

	@Override
	public void excluir(final ID id) {

		this.checkNullOrEmptyParameters(id);

		this.logger.debug("Excluindo entidade: [" + this.clazz.getName() + " - Id [" + id + "]");

		try {

			this.entityManager.getTransaction().begin();

			final T entity = this.selecionarPorIdParaExclusao(id);

			this.entityManager.remove(entity);

			this.entityManager.getTransaction().commit();
		} catch (Exception e) {

			this.entityManager.getTransaction().rollback();

			throw e;
		}

	}

	@Override
	public void excluirComVerificacaoDeConstraints(final ID id) {

		try {

			this.checkNullOrEmptyParameters(id);

			this.logger.debug("Excluindo entidade: [" + this.clazz.getName() + " - Id [" + id + "]");

			this.excluir(id);

			this.entityManager.flush();
		} catch (final Exception e) {

			Throwable throwable = e.getCause();

			while (!IsNullUtil.isNullOrEmpty(throwable) && !( throwable instanceof ConstraintViolationException )) {

				throwable = throwable.getCause();
			}

			if (throwable instanceof ConstraintViolationException) {

				throw new BusinessException("XXXX", "Erro ao excluir item com registros associados", e);
			}
		}
	}

	private T selecionarPorIdParaExclusao(final ID id) {

		this.checkNullOrEmptyParameters(id);

		this.logger.debug("Selecionando Entidade para Exclusão: [" + this.clazz.getName() + " - Id [" + id + "]");

		final T result = this.entityManager.find(this.clazz, id);

		ExceptionUtil.throwsIfTrue("XXXX", "Registro está nulo!", EntidadeUtil.isNullEntidade(result));

		return result;
	}

	@Override
	public void salvar(final List<T> entitys) {

		if (!IsNullUtil.isNullOrEmpty(entitys)) {

			entitys.stream().forEach(e -> this.salvar(e));
		}
	}

	@Override
	public void prepare(final T entity) {

		/**
		 * Situações onde tem a Entidade, mas o ID é nulo ou é um valor menor ou igual a ZERO
		 */
		if (!IsNullUtil.isNullOrEmpty(entity) && EntidadeUtil.isNullOrEmptyEntidade(entity)) {

			entity.setId(null);
		}
	}

	@Override
	public T selecionarPorIdFull(final ID id) {

		return this.selecionarPorId(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> selecionarPorId(final List<ID> ids) {

		this.logger.debug("Selecionando Entidades: [" + this.clazz.getName() + " - Ids " + ids);

		List<T> queryResult = null;

		if (!IsNullUtil.isNullOrEmpty(ids)) {

			final Criteria criteria = this.createCriteria();

			criteria.add(Restrictions.in("id", ids));

			queryResult = criteria.list();
		}

		return queryResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<T> buscarTodos(final String... ordenarPor) {

		this.logger.debug("Buscando todas entidades [" + this.clazz.getName() + "]");

		final Query query = this.createQuery(this.getHqlParaBuscaEntidade(ordenarPor));

		final List queryResult = query.getResultList();

		return IsNullUtil.isNullOrEmpty(queryResult) ? new ArrayList<>() : queryResult;
	}

	public void deleteTodos(final Collection<ID> ids) {

		if (IsNullUtil.isNotNullOrEmpty(ids)) {

			final Query query = this.createQuery("delete from " + this.clazz.getName() + " x where x.id in (:ids)");

			query.setParameter("ids", ids);

			try {

				this.entityManager.getTransaction().begin();

				query.executeUpdate();

				this.entityManager.getTransaction().commit();
			} catch (Exception e) {

				this.entityManager.getTransaction().rollback();

				logger.error("Erro ao excluir Objetos em massa", e);
			}

		}
	}

	@Override
	public Long count() {

		this.logger.debug("Contando todas entidades [" + this.clazz.getName() + "]");

		final Query query = this.createQuery(this.getHqlParaContarEntidade());

		final Object result = query.getSingleResult();

		return (Long) result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Long buscarAgregacao(final String nomeQueryNomeada, final QueryParameter parameters) {

		this.logger.debug("Buscando agregacao por Query Nomeada, Nome Query: [" + nomeQueryNomeada + "] - Parametros: " + parameters.toString());

		final Query query = this.createNamedQuery(nomeQueryNomeada);

		this.setParametrosQuery(query, parameters);

		final List queryResult = query.getResultList();

		return IsNullUtil.isNullOrEmpty(queryResult) ? 0 : ( (Long) queryResult.get(0) ).longValue();
	}

	@Override
	public void executaOperacaoEmLote(final String nomeOperacao, final QueryParameter parameters) {

		this.logger.debug("Executando Operacao em Lote, Nome Operacao: [" + nomeOperacao + "] - Parametros: " + parameters.toString());

		final Query query = this.createNamedQuery(nomeOperacao);

		this.setParametrosQuery(query, parameters);

		try {

			this.entityManager.getTransaction().begin();

			query.executeUpdate();

			this.entityManager.clear();

			this.entityManager.getTransaction().commit();

		} catch (Exception e) {

			e.printStackTrace();

			this.entityManager.getTransaction().rollback();
		}
	}

	@Override
	public void checkNullOrEmptyParameters(final Object... parameters) {

		if (IsNullUtil.isNullOrEmptyParameters(parameters)) {

			throw new ParameterNotFoundException();
		}
	}

	private Criteria createCriteria(final Entidade<ID> exemplo, final MatchMode matchMode, final boolean ignoreCase, final boolean filtraEntidadeAninhada, final String... ordenarPor) {

		Example example = Example.create(exemplo);

		if (IsNullUtil.isNotNull(matchMode)) {

			example = example.enableLike(this.getRealMatchMode(matchMode));
		}

		if (ignoreCase) {

			example = example.ignoreCase();
		}

		final Criteria criteria = this.createCriteria();

		criteria.add(example);

		if (!IsNullUtil.isNullOrEmpty(ordenarPor)) {

			for (final String order : ordenarPor) {

				criteria.addOrder(Order.asc(order));
			}

		}

		if (filtraEntidadeAninhada) {

			this.prencheEntidadeAninhadaNaCriteria(criteria, exemplo);
		}

		return criteria;
	}

	private void prencheEntidadeAninhadaNaCriteria(final Criteria criteria, final Entidade<ID> exemplo) {

		final Field[] fields = exemplo.getClass().getDeclaredFields();

		if (IsNullUtil.isNullOrEmpty(fields)) {

			return;
		}

		for (final Field item : fields) {

			if (this.isFieldDescendenteEntidade(item)) {

				Object entidade = null;

				try {

					final Method method = exemplo.getClass().getMethod(this.generateNameGetMethod(item.getName()), new Class[0]);

					entidade = method.invoke(exemplo, new Object[0]);
				} catch (final Exception e) {

					this.logger.error("Erro inesperado!", e);

					continue;
				}

				if (!IsNullUtil.isNullOrEmpty(entidade)) {

					final Serializable id = this.getIdEmbeddedEntidade(entidade);

					if (!IsNullUtil.isNullOrEmpty(id)) {

						criteria.add(Restrictions.eq(item.getName() + ".id", id));
					}
				}
			}
		}
	}

	protected boolean isFieldDescendenteEntidade(final Field field) {

		return field.getType().getSuperclass() == Entidade.class;
	}

	protected String generateNameGetMethod(final String nameField) {

		return "get" + nameField.substring(0, 1).toUpperCase() + nameField.substring(1);
	}

	@SuppressWarnings("unchecked")
	private Serializable getIdEmbeddedEntidade(final Object entidade) {

		return ( (Entidade<ID>) entidade ).getId();
	}

	protected Session getRealHibernateSession() {

		return (Session) this.entityManager.getDelegate();
	}

	protected Connection getRealSQLConnection() {

		Connection result = null;

		try {

			result = ( (SessionImpl) this.getRealHibernateSession() ).getJdbcConnectionAccess().obtainConnection();
		} catch (final SQLException e) {

			this.logger.error("Erro ao buscar SQL Connection", e);

			throw new PersistenceException(e);
		}

		return result;
	}

	@SuppressWarnings("deprecation")
	public Criteria createCriteria() {

		return this.getRealHibernateSession().createCriteria(this.clazz);
	}

	public Query createQuery(final String query) {

		this.entityManager.clear();

		return this.entityManager.createQuery(query);
	}

	public Query createNamedQuery(final String nomeQueryNomeada) {

		this.entityManager.clear();

		return this.entityManager.createNamedQuery(nomeQueryNomeada);
	}

	public org.hibernate.criterion.MatchMode getRealMatchMode(final MatchMode matchMode) {

		switch (matchMode) {
			case ANYWHERE:
				return org.hibernate.criterion.MatchMode.ANYWHERE;
			case END:
				return org.hibernate.criterion.MatchMode.END;
			case EXACT:
				return org.hibernate.criterion.MatchMode.EXACT;
			case START:
				return org.hibernate.criterion.MatchMode.START;
		}
		return null;
	}

	private String getHqlParaBuscaEntidade(final String... ordenarPor) {

		final StringBuilder hql = new StringBuilder();

		hql.append("select x from ").append(this.clazz.getName()).append(" x ");

		if (!IsNullUtil.isNullOrEmpty(ordenarPor)) {

			hql.append(" order by ").append(this.getFiltroOrdenacao(ordenarPor));
		}

		this.logger.debug("Hql de busca entidade: [" + hql + "]");

		return hql.toString();
	}

	private String getHqlParaContarEntidade() {

		final StringBuilder hql = new StringBuilder();

		hql.append("select count(x) from ").append(this.clazz.getName()).append(" x ");

		this.logger.debug("Hql de contagem entidade: [" + hql + "]");

		return hql.toString();
	}

	private String getFiltroOrdenacao(final String... ordenarPor) {

		if (IsNullUtil.isNullOrEmpty(ordenarPor)) {

			return null;
		}

		final StringBuilder ordenacao = new StringBuilder();

		for (final String item : ordenarPor) {

			ordenacao.append(item).append(", ");
		}

		final int ultimaVirgula = ordenacao.lastIndexOf(",");

		return ordenacao.delete(ultimaVirgula, ordenacao.length()).toString();
	}

	private void setParametrosQuery(final Query query, final QueryParameter params) {

		if (IsNullUtil.isNotNull(params)) {

			final Set<Entry<String, Object>> rawParametros = params.parameters().entrySet();

			for (final Entry<String, Object> entry : rawParametros) {

				query.setParameter(entry.getKey(), entry.getValue());
			}

		}

	}

	public Query createNativeQuery(final String query) {

		return this.entityManager.createNativeQuery(query);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<T> buscarPorQueryNomeada(final String nomeQueryNomeada, final QueryParameter parameters) {

		this.logger.debug("Buscando entidades por Query Nomeada, Nome Query: [" + nomeQueryNomeada + "] - " + parameters);

		final Query query = this.createNamedQuery(nomeQueryNomeada);

		this.setParametrosQuery(query, parameters);

		final List queryResult = query.getResultList();

		return IsNullUtil.isNullOrEmpty(queryResult) ? new ArrayList<>() : queryResult;
	}

	@Override
	public List<T> buscarPorQueryNomeada(final String nomeQueryNomeada) {

		return this.buscarPorQueryNomeada(nomeQueryNomeada, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <Obj> Obj buscarUniqPorGenericQueryNomeada(final String nomeQueryNomeada, final QueryParameter parameters) {

		this.logger.debug("Buscando valor por Uniq Query Nomeada, Nome Query: [" + nomeQueryNomeada + "] - " + parameters);

		final Query query = this.createNamedQuery(nomeQueryNomeada);

		query.setMaxResults(1);

		this.setParametrosQuery(query, parameters);

		final List queryResult = query.getResultList();

		return IsNullUtil.isNullOrEmpty(queryResult) ? null : (Obj) queryResult.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> buscarPorExemplo(final T exemplo, final MatchMode matchMode, final boolean ignoreCase, final boolean filtraEntidadeAninhada, final String... ordenarPor) {

		this.validaEntidadeNula(exemplo);

		final Criteria criteria = this.createCriteria(exemplo, matchMode, ignoreCase, filtraEntidadeAninhada, ordenarPor);

		return criteria.list();
	}

	@Override
	public List<T> buscarPorExemplo(final T exemplo, final MatchMode matchMode, final boolean ignoreCase, final String... ordenarPor) {

		return this.buscarPorExemplo(exemplo, matchMode, ignoreCase, false, ordenarPor);
	}

	@Override
	public List<T> buscarPorExemplo(final T exemplo, final boolean filtraEntidadeAninhada, final String... ordenarPor) {

		return this.buscarPorExemplo(exemplo, MatchMode.ANYWHERE, true, filtraEntidadeAninhada, ordenarPor);
	}

	@Override
	public List<T> buscarPorExemplo(final T exemplo, final String... ordenarPor) {

		return this.buscarPorExemplo(exemplo, false, ordenarPor);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <O> O buscarPorGenericQueryNomeada(final String nomeQueryNomeada, final QueryParameter parameters) {

		this.logger.debug("Buscando entidades por Generic Query Nomeada, Nome Query: [" + nomeQueryNomeada + "] - " + parameters);

		final Query query = this.createNamedQuery(nomeQueryNomeada);

		this.setParametrosQuery(query, parameters);

		final List queryResult = query.getResultList();

		return IsNullUtil.isNullOrEmpty(queryResult) ? (O) new ArrayList<O>() : (O) queryResult;
	}

	/**
	 * Descrição Padrão: <br>
	 * <br>
	 *
	 * {@inheritDoc}
	 *
	 * @see br.com.zurcs.comum.crud.CrudService#buscarPorGenericQueryNomeada(java.lang.String)
	 */
	@Override
	public <O> O buscarPorGenericQueryNomeada(final String nomeQueryNomeada) {

		return this.buscarPorGenericQueryNomeada(nomeQueryNomeada, QueryParameter.nullQueryParameter());
	}

	protected void raiseEntityNotFoundException() {

		throw new EntityNotFoundException("XXXX", "Entidade é nula.", SeverityException.ATENCAO);
	}

}
