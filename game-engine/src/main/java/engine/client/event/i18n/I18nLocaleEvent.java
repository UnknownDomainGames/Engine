package engine.client.event.i18n;

import engine.client.i18n.LocaleDefinition;
import engine.event.Event;

public abstract class I18nLocaleEvent implements Event {

    private final LocaleDefinition locale;

    public I18nLocaleEvent(LocaleDefinition locale) {
        this.locale = locale;
    }

    public LocaleDefinition getLocale() {
        return locale;
    }
}
