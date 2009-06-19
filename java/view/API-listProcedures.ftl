<?xml version="1.0" encoding="UTF-8"?>
<disaster-manager-api>
	<procedure-list>
		<#list procedures as procedure>
			<procedure id="${procedure.getID()}" name="${procedure.getName()}"
				state="<#if procedure.hasExecution()><#if !procedure.isExecutionShuttingDown()>running<#else>shutting-down</#if><#else>ready</#if>">
				<![CDATA[${procedure.getDescription()?replace("]]>", "]]]]><![CDATA[>")}]]>
			</procedure>
		</#list>
	</procedure-list>
</disaster-manager-api>
