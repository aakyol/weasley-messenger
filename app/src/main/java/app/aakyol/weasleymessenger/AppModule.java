package app.aakyol.weasleymessenger;

import android.content.Context;

import javax.inject.Named;

import app.aakyol.weasleymessenger.activity.ActivityListRecipients;
import app.aakyol.weasleymessenger.service.OnBootService;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context context = null;

    public AppModule (Context context) {
        this.context = context;
    }

    @Provides
    @Named("dbContext")
    Context getContext() {
        return context;
    }

}
