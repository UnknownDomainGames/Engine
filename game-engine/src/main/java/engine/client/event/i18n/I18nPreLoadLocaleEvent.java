package engine.client.event.i18n;

import engine.client.i18n.LocaleDefinition;

public class I18nPreLoadLocaleEvent extends I18nLocaleEvent {

    public I18nPreLoadLocaleEvent(LocaleDefinition locale) {
        super(locale);
    }
}
