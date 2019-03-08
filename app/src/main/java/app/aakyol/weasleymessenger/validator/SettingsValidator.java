package app.aakyol.weasleymessenger.validator;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.aakyol.weasleymessenger.resource.AppResources;

@Singleton
public class SettingsValidator {

    @Inject
    public SettingsValidator() {}

    public Boolean ifAllFieldsAreEmpty(final String interval, final String accuracy) {
        return (interval.isEmpty() && accuracy.isEmpty());
    }

    public Boolean ifIntervalIsEmpty(final String interval) {
        return interval.isEmpty();
    }

    public Boolean ifAccuracyIsSame(final String accuracy) {
        return accuracy.equals(AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY);
    }

}
