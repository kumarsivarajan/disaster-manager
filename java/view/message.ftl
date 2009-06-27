<p>Wiadomości:</p>
<table>
<#list messages as msg>
	<tr>
		<td>
			<a href="${msg.getID()}">
				<#if !msg.read()><b>Wiadomość nr ${msg.getID()}</b>
				<#else>Wiadomość nr ${msg.getID()}
				</#if>
			</a>
		</td>
		<td>
			${msg.getDate()}
		</td>
	</tr>
</#list>
</table>
