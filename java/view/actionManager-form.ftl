<h2>Edycja akcji nr ${action.getID()}</h2>

<#if !action.getProcedure().isAdded()>
<p>Procedura NIE JEST jeszcze dodana.</p>
</#if>

<#if !action.isAdded()>
<p>Akcja NIE JEST jeszcze dodana - kliknij "Zapisz".</p>
</#if>

<form action="/actionManagement/edit/${action.getID()}/" method="post">
	<p>Etykieta: <input type="text" name="label" <#if action.getLabel()??> value="${action.getLabel()?html}" </#if> /></p>
	<p>Ograniczenie czasu (sekundy): <input type="text" name="maxtime" <#if action.getMaxTime()??> value="${action.getMaxTime()}" </#if> /></p>

	<hr />

	<h3>Parametry akcji</h3>

	${paramForm}

	<p><input type="submit" value="Zapisz" /></p>
	<p><a href="/procedureManagement/edit/${action.getProcedure().getID()}/">Powrót do procedury</a></p>

</form>
