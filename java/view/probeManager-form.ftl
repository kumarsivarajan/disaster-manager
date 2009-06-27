<h2>Edycja czujnika nr ${probe.getID()}</h2>

<form action="/probeManagement/edit/${probe.getID()}/" method="post">
	<p>Nazwa: <input type="text" name="name" value="${probe.getName()?html}" /></p>
	<p>Okres sprawdzania: <input type="text" name="interval" value="${probe.getInterval()}" /></p>
	<p>
		Numer portu:
		<select name="port">
			<option <#if probe.getPort() = 0> selected="selected" </#if> >0</option>
			<option <#if probe.getPort() = 1> selected="selected" </#if> >1</option>
			<option <#if probe.getPort() = 2> selected="selected" </#if> >2</option>
			<option <#if probe.getPort() = 3> selected="selected" </#if> >3</option>
			<option <#if probe.getPort() = 4> selected="selected" </#if> >4</option>
			<option <#if probe.getPort() = 5> selected="selected" </#if> >5</option>
			<option <#if probe.getPort() = 6> selected="selected" </#if> >6</option>
			<option <#if probe.getPort() = 7> selected="selected" </#if> >7</option>
		</select>
	</p>

	<p>
		Czekaj na:
		<input type="checkbox" name="state" id="probeManager-state"
			<#if probe.getState()> checked="checked" </#if>
		/>
		<label for="probeManager-state">
			włączenie
		</label>
	</p>

	<p>
		Uruchamiana procedura:
		<select name="proc">
			<#if !probe.getProcedure()??>
				<option selected="selected" value="0">nie wybrano</option>
			</#if>

			<#list procedures as procedure>
				<option value="${procedure.getID()}"
				<#if probe.getProcedure()?? && probe.getProcedure().getID() == procedure.getID()> selected="selected" </#if>
				>${procedure.getName()}</option>
			</#list>
		</select>
	</p>

	<p>
		<input type="submit" value="Zapisz" />
	</p>
</form>
