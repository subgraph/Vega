package com.subgraph.vega.internal.http.proxy.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

public class CertificateSigner {
    private final X500Name agent;
    private final AlgorithmId algid;

    public AlgorithmId getAlgorithmId() {
            return algid;
    }

    public X500Name getSigner() {
            return agent;
    }

    CertificateSigner(Signature sig, X500Name agent) {
            this.agent = agent;
            try {
                    this.algid = AlgorithmId.get(sig.getAlgorithm());
            } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("Algorithm not found: "+ e.getMessage());
            }
    }
}
