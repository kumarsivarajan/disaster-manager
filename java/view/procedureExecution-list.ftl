<h2>Lista procedur</h2>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Nazwa</th>
			<th>Status</th>
			<th>Akcje</th>
		</tr>
	</thead>
	<tbody>
		<#list procedures as procedure>
			<tr>
				<td>${procedure.getID()}</td>
				<td>${procedure.getName()}</td>
				<td>
					<#if procedure.hasExecution()>
						<#if procedure.isExecutionShuttingDown()>
							zatrzymywana
						<#else>
							uruchomiona
						</#if>
					<#else>
						zatrzymana
					</#if>
				</td>
				<td>
					<#if procedure.hasExecution()>
						<#if !procedure.isExecutionShuttingDown()>
							<a href="/procedureExecution/shutdown/${procedure.getID()}/">zatrzymaj</a>
						</#if>
					<#else>
						<a href="/procedureExecution/run/${procedure.getID()}/">uruchom</a>
					</#if>
				</td>
			</tr>
		</#list>
	</tbody>
</table>