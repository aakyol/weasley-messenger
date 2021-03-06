package app.aakyol.weasleymessenger;

import javax.inject.Singleton;

import app.aakyol.weasleymessenger.helper.DBHelper;
import app.aakyol.weasleymessenger.validator.RecipientValidator;
import app.aakyol.weasleymessenger.validator.SettingsValidator;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    DBHelper getDBHelper();

    RecipientValidator getRecipientValidator();

    SettingsValidator getSettingsValidator();
}
