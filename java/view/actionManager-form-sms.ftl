<p>Odbiorca: <input type="text" name="actionParam-recipients" value="${action.getRecipient()?html}" /></p>
<#--
	<p>Nadawca: <input type="text" name="actionParam-subject" value="${action.getFrom()?html}" /></p>
-->
<p>Treść SMSa:</p>
<textarea name="actionParam-message" cols="50" rows="3">${action.getMessage()?html}</textarea>