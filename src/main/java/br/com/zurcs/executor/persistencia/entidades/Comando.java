package br.com.zurcs.executor.persistencia.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.zurcs.base.entity.Entidade;

/**
 * <p>
 * <b>Title:</b> Comando.java
 * </p>
 * 
 * <p>
 * <b>Description:</b>Entidade que representa um comando para ser executado
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
@Table(name = "comando")
public class Comando implements Entidade<Integer> {

	/** Atributo serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "ds_nome", nullable = false, updatable = false, length = 100)
	private String nome;

	@Column(name = "ds_comando", nullable = false, length = 500)
	private String comando;

	@JoinColumn(name = "modulo_id", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private Modulo modulo;

	public Comando() {

	}

	public Comando( Integer id ) {
		this.id = id;

	}

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
	 * Retorna o valor do atributo <code>modulo</code>
	 *
	 * @return <code>Modulo</code>
	 */
	public Modulo getModulo() {

		return modulo;
	}

	/**
	 * Define o valor do atributo <code>modulo</code>.
	 *
	 * @param modulo
	 */
	public void setModulo(Modulo modulo) {

		this.modulo = modulo;
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
	 * Retorna o valor do atributo <code>comando</code>
	 *
	 * @return <code>String</code>
	 */
	public String getComando() {

		return comando;
	}

	/**
	 * Define o valor do atributo <code>comando</code>.
	 *
	 * @param comando
	 */
	public void setComando(String comando) {

		this.comando = comando;
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

		return "Comando [id=" + id + ", nome=" + nome + ", comando=" + comando + "]";
	}

}
