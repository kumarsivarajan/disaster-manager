<p>Odbiorca: <input type="text" name="actionParam-address" value="${action.getAddress()?html}" /></p>
<p>Temat wiadomości: <input type="text" name="actionParam-subject" value="${action.getSubject()?html}" /></p>
<p>Treść wiadomości:</p>
<textarea name="actionParam-message" cols="50" rows="3">${action.getMessage()?html}</textarea>