package engine.client.event.i18n;

import engine.client.i18n.LocaleDefinition;

public class I18nPostLoadLocaleEvent extends I18nLocaleEvent {

    public I18nPostLoadLocaleEvent(LocaleDefinition locale) {
        super(locale);
    }
}
