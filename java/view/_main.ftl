<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="pl" lang="pl">
<head>
	<title>${title}</title>
	<meta http-equiv="Content-Type" content="${contentType}; charset=utf-8" />

	<#--
		<script charset="utf-8" type="text/javascript" src="/disaster-manager-1240440294.js"></script>
	-->

	<link rel="stylesheet" href="/static/style.css" type="text/css" />
	<link rel="shortcut icon" href="/favicon.ico" />

</head>
<body>
<div id="TOP">
	Disaster Manager
</div>
<div id="MENU">
	<ul id="mainmenu">
		<#list menubuttons as button>
			<li class="menuitem"><a href="${button.url}">${button.caption}</a></li>
		</#list>
	</ul>
</div>
<div id="MAIN">
	${contents}
</div>
<div id="FEET">
</div>
</body>
</html>