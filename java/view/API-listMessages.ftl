<?xml version="1.0" encoding="UTF-8"?>
<disaster-manager-api>
	<message-list>
		<#list messages as message>
			<message id="${message.getID()}" date="${message.getDate()}">
				<![CDATA[${message.getMessage()?replace("]]>", "]]]]><![CDATA[>")}]]>
			</message>
		</#list>
	</message-list>
</disaster-manager-api>
