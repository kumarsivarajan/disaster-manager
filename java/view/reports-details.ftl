<h2>Raport wykonania procedury</h2>

<ul>
	<li>
		Procedura:
		<#if report.getProcedure()??>
			<a href="/procedureManagement/edit/${report.getProcedure().getID()}/">
				${report.getProcedure().getName()}
			</a>
		<#else>
			${report.getProcedureName()}
		</#if>
	</li>
	<li>Data: ${report.getDate()}</li>
	<li>
		Status:
		<#if report.isFinished()>
			<#if report.getErrorMessage()??>
				błąd
			<#else>
				gotowy
			</#if>
		<#else>
			w trakcie
		</#if>
	</li>
</ul>

<#if report.getErrorMessage()??>
	<h3>Komunikat błędu</h3>
	<p>${report.getErrorMessage()?html}</p>
</#if>

<h3>Przebieg wykonania procedury</h3>

<table>
	<thead>
		<tr>
			<th>Typ</th>
			<#-- <th>Etykieta</th> -->
			<th>Czas wykonania</th>
			<th>Data</th>
		</tr>
	</thead>
	<tbody>
		<#list report.getLog() as reportAction>
			<tr>
				<td>${reportAction.getTypeName()}</td>
				<td <#if reportAction.haveTimeoutWarning()> class="warning" </#if> >
					${reportAction.getUsedTime()}s
					<#if reportAction.getMaxTime()??>
						/${reportAction.getMaxTime()}s
					</#if>
				</td>
				<td>${reportAction.getDate()}</td>
			</tr>
		</#list>
	</tbody>
</table>
