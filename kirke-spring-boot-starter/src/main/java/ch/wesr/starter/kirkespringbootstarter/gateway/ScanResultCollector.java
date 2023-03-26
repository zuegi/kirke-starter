package ch.wesr.starter.kirkespringbootstarter.gateway;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class ScanResultCollector {

    private static ScanResult scanResult;
    private static ScanResultCollector instance;

    private ScanResultCollector() {
        scanResult = scanResult();
    }

    public static ScanResultCollector getInstance() {
        if (instance == null) {
            instance = new ScanResultCollector();
        }
        return instance;
    }

    public ScanResult getResult() {
        return scanResult;
    }

    private static ScanResult scanResult() {
        return new ClassGraph()
                .disableJarScanning()
                .enableAllInfo()
                .scan();
    }
}
