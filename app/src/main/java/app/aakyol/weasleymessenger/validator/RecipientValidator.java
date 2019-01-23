package app.aakyol.weasleymessenger.validator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipientValidator {

    @Inject
    public RecipientValidator() {}

    public Boolean ifAnyFieldIsEmpty(final String alias, final String phoneNo, final String message, final String distance) {
        return (alias.isEmpty() || phoneNo.isEmpty() || message.isEmpty() || distance.isEmpty());
    }
}
