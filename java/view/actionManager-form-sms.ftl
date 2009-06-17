<p>Odbiorcy: <input type="text" name="actionParam-recipients" value="${action.getRecipients()?html}" /></p>
<p>Treść emaila:</p>
<textarea name="actionParam-message" cols="50" rows="3">${action.getMessage()?html}</textarea>