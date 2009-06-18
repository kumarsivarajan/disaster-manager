<h2>Raporty wykonania procedur</h2>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Procedura</th>
			<th>Data</th>
			<th>Status</th>
			<th>Szczegóły</th>
		</tr>
	</thead>
	<tbody>
		<#list reports as report>
			<tr>
				<td>${report.getID()}</td>
				<td>
					<#if report.getProcedure()??>
						<a href="/procedureManagement/edit/${report.getProcedure().getID()}/">
							${report.getProcedure().getName()}
						</a>
					<#else>
						${report.getProcedureName()}
					</#if>
				</td>
				<td>${report.getDate()}</td>
				<td>
					<#if report.isFinished()>
						<#if report.getErrorMessage()??>
							błąd
						<#else>
							gotowy
						</#if>
					<#else>
						w trakcie
					</#if>
				</td>
				<td>
					<a href="/reports/details/${report.getID()}/">
						szczegóły
					</a>
				</td>
			</tr>
		</#list>
	</tbody>
</table>