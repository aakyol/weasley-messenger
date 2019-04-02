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

    public Boolean ifIntervalIsSame(final String interval) {
        return interval.equals(String.valueOf(AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL / (60 * 1000)));
    }

    public Boolean ifAccuracyIsSame(final String accuracy) {
        return accuracy.equals(AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY);
    }

}
