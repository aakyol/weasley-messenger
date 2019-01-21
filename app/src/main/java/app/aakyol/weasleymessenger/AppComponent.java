package app.aakyol.weasleymessenger;

import javax.inject.Singleton;

import app.aakyol.weasleymessenger.helper.DBHelper;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    DBHelper getDBHelper();
}
