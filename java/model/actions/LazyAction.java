package model.actions;

import model.*;

/**
 * Klasa tymczasowych obiektów, wskazujących następną akcję
 *
 * @author tomkiewicz
 */
public class LazyAction extends Action
{
	public LazyAction(Procedure proc, int id)
	{
		super(proc);
		this.id = id;
	}

	public ActionType getType()
	{
		throw new AssertionError("Klasa LazyAction nie obsługuje tej metody");
	}

	public String getArguments()
	{
		throw new AssertionError("Klasa LazyAction nie obsługuje tej metody");
	}

	protected void setArguments(String arguments)
	{
		throw new AssertionError("Klasa LazyAction nie obsługuje tej metody");
	}

	public void doAction(ProcedureExecution procExec)
	{
		throw new AssertionError("Klasa LazyAction nie obsługuje tej metody");
	}
}
