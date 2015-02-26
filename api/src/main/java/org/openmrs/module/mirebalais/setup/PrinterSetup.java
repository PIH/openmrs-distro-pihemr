package org.openmrs.module.mirebalais.setup;

import org.openmrs.module.mirebalais.printer.GX430tPrintHandler;
import org.openmrs.module.mirebalais.printer.HC100PrintHandler;
import org.openmrs.module.mirebalais.printer.P110iPrintHandler;
import org.openmrs.module.mirebalais.printer.ZXPSeries3PrintHandler;
import org.openmrs.module.printer.PrinterService;

public class PrinterSetup {

    public static void registerPrintHandlers(PrinterService printerService) {
        printerService.registerPrintHandler(new P110iPrintHandler());
        printerService.registerPrintHandler(new ZXPSeries3PrintHandler());
        printerService.registerPrintHandler(new GX430tPrintHandler());
        printerService.registerPrintHandler(new HC100PrintHandler());
    }

}

