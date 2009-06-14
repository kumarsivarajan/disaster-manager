<p>Odbiorcy: <input type="text" name="actionParam-addresses" value="${action.getAddresses()?html}" /></p>
<p>Tytuł: <input type="text" name="actionParam-subject" value="${action.getSubject()?html}" /></p>
<p>Treść emaila:</p>
<textarea name="actionParam-message" cols="50" rows="3">${action.getMessage()?html}</textarea>