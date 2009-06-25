<p>
	Numer portu:
	<select name="port">
		<option <#if action.getPort() = 0> selected="selected" </#if> >0</option>
		<option <#if action.getPort() = 1> selected="selected" </#if> >1</option>
		<option <#if action.getPort() = 2> selected="selected" </#if> >2</option>
		<option <#if action.getPort() = 3> selected="selected" </#if> >3</option>
		<option <#if action.getPort() = 4> selected="selected" </#if> >4</option>
		<option <#if action.getPort() = 5> selected="selected" </#if> >5</option>
		<option <#if action.getPort() = 6> selected="selected" </#if> >6</option>
		<option <#if action.getPort() = 7> selected="selected" </#if> >7</option>
	</select>
</p>

<p>
	Czekaj na
	<input type="checkbox" name="on" id="actionManager-serialProbeGet-on"
		<#if action.getOn()> checked="checked" </#if>
	/>
	<label for="actionManager-serialProbeGet-on">
		włączenie
	</label>
</p>
