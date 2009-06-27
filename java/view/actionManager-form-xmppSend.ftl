<p>Odbiorca: <input type="text" name="actionParam-addresses" <#if action.getAddresses()??> value="${action.getAddresses()?html}" </#if> /></p>
<p>Treść wiadomości:</p>
<textarea name="actionParam-message" cols="50" rows="3">${action.getMessage()?html}</textarea>