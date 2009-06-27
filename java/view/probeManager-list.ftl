<h2>Lista procedur</h2>

<p><a href="/probeManagement/add/">Dodaj nową</a></p>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Nazwa</th>
			<th>Akcje</th>
		</tr>
	</thead>
	<tbody>
		<#list probes as probe>
			<tr>
				<td>${probe.getID()}</td>
				<td><#if probe.getName() == ""><em>(nie nazwany)</em><#else>${probe.getName()}</#if></td>
				<td>
					<a href="/probeManagement/delete/${probe.getID()}/">usuń</a>
					<a href="/probeManagement/edit/${probe.getID()}/">edytuj</a>
				</td>
			</tr>
		</#list>
	</tbody>
</table>