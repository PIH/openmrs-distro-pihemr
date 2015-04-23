package org.openmrs.module.mirebalais.printer.impl;

import org.apache.commons.io.IOUtils;
import org.openmrs.util.OpenmrsUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulates a printer listening on a network port
 */
public class TestPrinter {

    private static final String TERMINATE_CODE = "TERMINATE";

    private String ipAddress;
    private int port;
    private String encoding;

    private boolean started = false;
    private List<PrintJob> printJobs = new ArrayList<PrintJob>();

    public List<PrintJob> getPrintJobs() {
        return printJobs;
    }

    public int getNumPrintJobs() {
        return printJobs.size();
    }

    public PrintJob getLatestPrintJob() {
        int numJobs = getNumPrintJobs();
        return numJobs > 0 ? printJobs.get(numJobs-1) : null;
    }

    public TestPrinter(String ipAddress, int port, String encoding) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.encoding = encoding;
    }

    public void start() throws Exception {
        started = true;
        PrintThread printThread = new PrintThread();
        printThread.start();
    }

    public void stop() throws Exception {
        started = false;
        print(TERMINATE_CODE);
    }

    /**
     * Start up the printer
     */
    public void print(String data) throws Exception {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port), 1000);
            IOUtils.write(data, socket.getOutputStream());
        }
        finally {
            socket.close();
        }
    }

    public class PrintThread extends Thread {

        @Override
        public void run() {
            while (started) {
                ServerSocket serverSocket = null;
                Socket clientSocket = null;
                BufferedReader in = null;
                try {
                    serverSocket = new ServerSocket(port);
                    clientSocket = serverSocket.accept();
                    PrintJob printJob = new PrintJob();
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), encoding));
                    for (String inputLine = in.readLine(); inputLine != null; inputLine = in.readLine()) {
                        printJob.addLine(inputLine.toString());
                    }
                    if (!printJob.toString().equals(TERMINATE_CODE)) {
                        printJobs.add(printJob);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException("Error running print thread", e);
                }
                finally {
                    IOUtils.closeQuietly(in);
                    try {
                        clientSocket.close();
                    }
                    catch (Exception e) {
                    }
                    try {
                        serverSocket.close();
                    }
                    catch (Exception e) {
                    }
                }
            }
        };
    }

    public class PrintJob {

        private List<String> data = new ArrayList<String>();

        public PrintJob() {}

        @Override
        public String toString() {
            return OpenmrsUtil.join(data, System.getProperty("line.separator"));
        }

        public void addLine(String dataLine) {
            data.add(dataLine);
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        public boolean containsData(String toTest) {
            for (String line : data) {
                if (line.contains(toTest)) {
                    return true;
                }
            }
            return false;
        }
    }
}
