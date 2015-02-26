package org.openmrs.module.mirebalais.printer;

import org.openmrs.module.printer.handler.SocketPrintHandler;

public class GX430tPrintHandler extends SocketPrintHandler {

    @Override
    public String getDisplayName() {
        return "GX430t Print Handler";
    }

    @Override
    public String getBeanName() {
        return "gx430tPrintHandler";
    }

    // just a simple socket print handlers

}
