package org.openmrs.module.mirebalais.printer;

import org.openmrs.module.printer.handler.SocketPrintHandler;

public class HC100PrintHandler extends SocketPrintHandler {

    @Override
    public String getDisplayName() {
        return "HC100 Print Handler";
    }

    @Override
    public String getBeanName() {
        return "hc100PrintHandler";
    }

    // just a simple socket print handlers

}
