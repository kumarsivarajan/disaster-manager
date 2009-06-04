<h2>Edycja procedury nr ${procedure.getID()}</h2>

<#if !procedure.isAdded()>
<p>Procedura NIE JEST jeszcze dodana - kliknij "Zapisz".</p>
</#if>

<form action="/procedureManagement/edit/${procedure.getID()}/" method="post">
	<p>Nazwa: <input type="text" name="name" <#if procedure.isAdded()> value="${procedure.getName()?html}" </#if> /></p>
	<p>Opis: <br /> <textarea name="description" cols="80" rows="5"><#if procedure.isAdded()>${procedure.getDescription()?html}</#if></textarea></p>
	<p>
		<input type="checkbox" id="procMgmt-edit-active" name="active" <#if procedure.isActive()> checked="checked" </#if> />
		<label for="procMgmt-edit-active">procedura aktywna</label>
	</p>

	<p>
		<input type="submit" value="Zapisz" />
	</p>
</form>

<h3>Akcje</h3>

<p><a href="/actionManagement/add/${procedure.getID()}/">dodaj</a></p>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Typ</th>
			<th>Etykieta</th>
			<th>Maksymalny czas</th>
			<th>Edycja</th>
		</tr>
	</thead>
	<tbody>
		<#list procedure.getActions() as action>
			<tr>
				<td>${action.getID()}</td>
				<td>---</td>
				<td><#if action.getLabel() == null>${action.getLabel()}<#else>---</#if></td>
				<td><#if action.getMaxTime() == null>${action.getMaxTime()}s<#else>---</#if></td>
				<td>
					<a href="/actionManagement/delete/${action.getID()}/">usuń</a>
					<a href="/actionManagement/edit/${action.getID()}/">edytuj</a>
				</td>
			</tr>
		</#list>
	</tbody>
</table>