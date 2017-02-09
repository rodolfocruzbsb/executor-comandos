package br.com.zurcs.executor.persistencia.hibernate.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.internal.util.StringHelper;

public class CustomH2Dialect extends H2Dialect {

	@Override
	public String getDropSequenceString(String sequenceName) {

		// Adding the "if exists" clause to avoid warnings
		return "drop sequence if exists " + sequenceName;
	}

	@Override
	public String getCreateTableString() {

		return "create table if not exists";
	}

	@Override
	public boolean dropConstraints() {

		// We don't need to drop constraints before dropping tables; that just
		// leads to error messages about missing tables when we don't have a
		// schema in the database
		return false;
	}

	@Override
	public boolean supportsIfExistsBeforeTableName() {

		return true;
	}

	@Override
	public boolean supportsIfExistsAfterTableName() {

		return false;
	}

	@Override
	public String getCascadeConstraintsString() {

		return " CASCADE ";
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {

		return new StringBuilder(30)

				.append(" add constraint ")

				.append(" if not exists ")

				.append(quote(constraintName))

				.append(" ")

				.append(foreignKeyDefinition)

				.toString();
	}
	
	public String getAddForeignKeyConstraintString(
			String constraintName,
			String[] foreignKey,
			String referencedTable,
			String[] primaryKey,
			boolean referencesPrimaryKey) {
		final StringBuilder res = new StringBuilder( 30 );

		res.append( " add constraint if not exists " )
				.append( quote( constraintName ) )
				.append( " foreign key (" )
				.append( StringHelper.join( ", ", foreignKey ) )
				.append( ") references " )
				.append( referencedTable );

		if ( !referencesPrimaryKey ) {
			res.append( " (" )
					.append( StringHelper.join( ", ", primaryKey ) )
					.append( ')' );
		}

		return res.toString();
	}
}
