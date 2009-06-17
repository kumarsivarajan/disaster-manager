<h2>Lista procedur</h2>

<p><a href="/procedureManagement/add/">Dodaj nową</a></p>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Nazwa</th>
			<th>Aktywna</th>
			<th>Akcje</th>
		</tr>
	</thead>
	<tbody>
		<#list procedures as procedure>
			<tr>
				<td>${procedure.getID()}</td>
				<td>${procedure.getName()}</td>
				<td><#if procedure.isActive()>TAK<#else>NIE</#if></td>
				<td>
					<a href="/procedureManagement/delete/${procedure.getID()}/">usuń</a>
					<a href="/procedureManagement/edit/${procedure.getID()}/">edytuj</a>
				</td>
			</tr>
		</#list>
	</tbody>
</table>