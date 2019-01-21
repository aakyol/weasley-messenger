package app.aakyol.weasleymessenger;

import android.content.Context;

import javax.inject.Named;

import app.aakyol.weasleymessenger.activity.ActivityListRecipients;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Named("dbContext")
    Context getListActivityContext() {
        return ActivityListRecipients.getContext();
    }

}
