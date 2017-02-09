package br.com.zurcs.executor.persistencia.entidades;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.zurcs.base.entity.Entidade;

/**
 * <p>
 * <b>Title:</b> Modulo.java
 * </p>
 * 
 * <p>
 * <b>Description:</b> Entidade que representa um modulo/agrupador de comandos
 * </p>
 * 
 * <p>
 * <b>Company: </b> Rodolfo Cruz TI
 * </p>
 * 
 * @author Rodolfo Cruz - rodolfocruz.ti@gmail.com
 * 
 * @version 1.0.0
 */
@Entity
@Table(name = "modulo")
public class Modulo implements Entidade<Integer> {

	/** Atributo serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "ds_nome", nullable = false, length = 100, updatable = false)
	private String nome;

	@Column(name = "ds_descricao", nullable = false, length = 250)
	private String descricao;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comando> comandos;

	/**
	 * Retorna o valor do atributo <code>id</code>
	 *
	 * @return <code>Integer</code>
	 */
	public Integer getId() {

		return id;
	}

	/**
	 * Define o valor do atributo <code>id</code>.
	 *
	 * @param id
	 */
	public void setId(Integer id) {

		this.id = id;
	}

	/**
	 * Retorna o valor do atributo <code>nome</code>
	 *
	 * @return <code>String</code>
	 */
	public String getNome() {

		return nome;
	}

	/**
	 * Define o valor do atributo <code>nome</code>.
	 *
	 * @param nome
	 */
	public void setNome(String nome) {

		this.nome = nome;
	}

	/**
	 * Retorna o valor do atributo <code>descricao</code>
	 *
	 * @return <code>String</code>
	 */
	public String getDescricao() {

		return descricao;
	}

	/**
	 * Define o valor do atributo <code>descricao</code>.
	 *
	 * @param descricao
	 */
	public void setDescricao(String descricao) {

		this.descricao = descricao;
	}

	/**
	 * Retorna o valor do atributo <code>comandos</code>
	 *
	 * @return <code>List<Comando></code>
	 */
	public List<Comando> getComandos() {

		return comandos;
	}

	/**
	 * Define o valor do atributo <code>comandos</code>.
	 *
	 * @param comandos
	 */
	public void setComandos(List<Comando> comandos) {

		this.comandos = comandos;
	}

	/**
	 * Descrição Padrão: <br>
	 * <br>
	 *
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "Modulo [id=" + id + ", nome=" + nome + ", descricao=" + descricao + ", comandos=" + comandos + "]";
	}

}
